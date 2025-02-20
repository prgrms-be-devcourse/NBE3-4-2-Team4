package com.NBE3_4_2_Team4.standard.util;

import com.NBE3_4_2_Team4.global.config.AppConfig;
import lombok.SneakyThrows;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ut {
    public static class str {
        public static boolean isBlank(String str) {
            return str == null || str.trim().isEmpty();
        }

        public static String lcfirst(String str) {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }
    }

    public static class file {
        private static final String ORIGINAL_FILE_NAME_SEPARATOR = "--originalFileName_";

        private static final Map<String, String> MIME_TYPE_MAP = new LinkedHashMap<>() {{
            put("application/json", "json");
            put("text/plain", "txt");
            put("text/html", "html");
            put("text/css", "css");
            put("application/javascript", "js");
            put("image/jpeg", "jpg");
            put("image/png", "png");
            put("image/gif", "gif");
            put("image/webp", "webp");
            put("image/svg+xml", "svg");
            put("application/pdf", "pdf");
            put("application/xml", "xml");
            put("application/zip", "zip");
            put("application/gzip", "gz");
            put("application/x-tar", "tar");
            put("application/x-7z-compressed", "7z");
            put("application/vnd.rar", "rar");
            put("audio/mpeg", "mp3");
            put("audio/mp4", "m4a");
            put("audio/x-m4a", "m4a");
            put("audio/wav", "wav");
            put("video/quicktime", "mov");
            put("video/mp4", "mp4");
            put("video/webm", "webm");
            put("video/x-msvideo", "avi");
        }};

        @SneakyThrows
        public static String downloadByHttp(String url, String dirPath) {
            return downloadByHttp(url, dirPath, true);
        }


        @SneakyThrows
        public static String downloadByHttp(String url, String dirPath, boolean uniqueFilename) {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            String tempFilePath = dirPath + "/" + UUID.randomUUID() + ".tmp";

            Ut.file.mkdir(dirPath);

            // 실제 파일 다운로드
            HttpResponse<Path> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofFile(Path.of(tempFilePath))
            );

            // 파일 확장자 추출
            String extension = getExtensionFromResponse(response);

            if (extension.equals("tmp")) {
                extension = getExtensionByTika(tempFilePath);
            }

            // 파일명 추출
            String filename = getFilenameWithoutExtFromUrl(url);

            filename = uniqueFilename
                    ? UUID.randomUUID() + ORIGINAL_FILE_NAME_SEPARATOR + filename
                    : filename;

            String newFilePath = dirPath + "/" + filename + "." + extension;

            mv(tempFilePath, newFilePath);

            return newFilePath;
        }

        public static String getExtensionByTika(String filePath) {
            String mineType = AppConfig.getTika().detect(filePath);

            return MIME_TYPE_MAP.getOrDefault(mineType, "tmp");
        }

        @SneakyThrows
        public static void mv(String oldFilePath, String newFilePath) {
            mkdir(Paths.get(newFilePath).getParent().toString());

            Files.move(
                    Path.of(oldFilePath),
                    Path.of(newFilePath),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        @SneakyThrows
        private static void mkdir(String dirPath) {
            Path path = Path.of(dirPath);

            if (Files.exists(path)) return;

            Files.createDirectories(path);
        }

        private static String getFilenameWithoutExtFromUrl(String url) {
            try {
                String path = new URI(url).getPath();
                String filename = Path.of(path).getFileName().toString();
                // 확장자 제거
                return filename.contains(".")
                        ? filename.substring(0, filename.lastIndexOf('.'))
                        : filename;
            } catch (URISyntaxException e) {
                // URL에서 파일명을 추출할 수 없는 경우 타임스탬프 사용
                return "download_" + System.currentTimeMillis();
            }
        }

        private static String getExtensionFromResponse(HttpResponse<?> response) {
            return response.headers()
                    .firstValue("Content-Type")
                    .map(contentType -> MIME_TYPE_MAP.getOrDefault(contentType, "tmp"))
                    .orElse("tmp");
        }

        @SneakyThrows
        public static void delete(String filePath) {
            Files.deleteIfExists(Path.of(filePath));
        }

        public static String getOriginalFileName(String filePath) {
            String originalFileName = Path.of(filePath).getFileName().toString();

            return originalFileName.contains(ORIGINAL_FILE_NAME_SEPARATOR)
                    ? originalFileName.substring(originalFileName.indexOf(ORIGINAL_FILE_NAME_SEPARATOR) + ORIGINAL_FILE_NAME_SEPARATOR.length())
                    : originalFileName;
        }

        public static String getFileExt(String filePath) {
            String filename = getOriginalFileName(filePath);

            return filename.contains(".")
                    ? filename.substring(filename.lastIndexOf('.') + 1)
                    : "";
        }

        @SneakyThrows
        public static int getFileSize(String filePath) {
            return (int) Files.size(Path.of(filePath));
        }

        @SneakyThrows
        public static void rm(String filePath) {
            Path path = Path.of(filePath);

            if (!Files.exists(path)) return;

            if (Files.isRegularFile(path)) {
                // 파일이면 바로 삭제
                Files.delete(path);
            } else {
                // 디렉터리면 내부 파일들 삭제 후 디렉터리 삭제
                Files.walkFileTree(path, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }

        public static String getFileExtTypeCodeFromFileExt(String ext) {
            return switch (ext) {
                case "jpeg", "jpg", "gif", "png", "svg", "webp" -> "img";
                case "mp4", "avi", "mov" -> "video";
                case "mp3", "m4a" -> "audio";
                default -> "etc";
            };
        }

        public static String getFileExtType2CodeFromFileExt(String ext) {
            return switch (ext) {
                case "jpeg", "jpg" -> "jpg";
                default -> ext;
            };
        }

        public static Map<String, Object> getMetadata(String filePath) {
            String ext = getFileExt(filePath);
            String fileExtTypeCode = getFileExtTypeCodeFromFileExt(ext);

            if (fileExtTypeCode.equals("img")) return getImgMetadata(filePath);

            return Map.of();
        }

        private static Map<String, Object> getImgMetadata(String filePath) {
            Map<String, Object> metadata = new LinkedHashMap<>();

            try (ImageInputStream input = ImageIO.createImageInputStream(new File(filePath))) {
                Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

                if (!readers.hasNext()) {
                    throw new IOException("지원되지 않는 이미지 형식: " + filePath);
                }

                ImageReader reader = readers.next();
                reader.setInput(input);

                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                metadata.put("width", width);
                metadata.put("height", height);

                reader.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return metadata;
        }

        @SneakyThrows
        public static String toFile(MultipartFile multipartFile, String dirPath) {
            if (multipartFile == null) return "";
            if (multipartFile.isEmpty()) return "";

            String filePath = dirPath + "/" + UUID.randomUUID() + ORIGINAL_FILE_NAME_SEPARATOR + multipartFile.getOriginalFilename();

            Ut.file.mkdir(dirPath);
            multipartFile.transferTo(new File(filePath));

            return filePath;
        }

        @SneakyThrows
        public static void copy(String filePath, String newFilePath) {
            mkdir(Paths.get(newFilePath).getParent().toString());

            Files.copy(
                    Path.of(filePath),
                    Path.of(newFilePath),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        public static String getContentType(String fileExt) {
            return MIME_TYPE_MAP.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().equals(fileExt))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse("");
        }

        public static String withNewExt(String fileName, String fileExt) {
            return fileName.contains(".")
                    ? fileName.substring(0, fileName.lastIndexOf('.') + 1) + fileExt
                    : fileName + "." + fileExt;
        }
    }

    public static class cmd {
        public static void runAsync(String cmd) {
            new Thread(() -> {
                run(cmd);
            }).start();
        }

        public static void run(String cmd) {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder processBuilder;

                if (os.contains("win")) {
                    // Windows 시스템에서는 Git Bash 경로 사용
                    processBuilder = new ProcessBuilder("C:\\Program Files\\Git\\bin\\bash.exe", "-c", cmd);
                } else {
                    // macOS 또는 Linux 시스템에서는 bash 사용
                    processBuilder = new ProcessBuilder("bash", "-c", cmd);
                }

                Process process = processBuilder.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                // 프로세스의 출력 읽기
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // IntelliJ 콘솔에 출력
                }

                // 에러 출력 읽기
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line); // 에러 메시지를 IntelliJ 콘솔에 출력
                }

                process.waitFor(1, TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class date {
        public static String getCurrentDateFormatted(String pattern) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.format(new Date());
        }
    }

    public static class url {
        public static String encode(String str) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return str;
            }
        }

        public static String removeDomain(String url) {
            return url.replaceFirst("https?://[^/]+", "");
        }
    }

    public static class pageable{
        /**
         * Pageable 객체를 생성하는 유틸리티 클래스입니다.
         * 기본적으로 페이지 번호는 1, 페이지 크기는 10, 정렬 기준은 "id"로 설정됩니다.
         *
         * 정렬 기준은 엔티티의 필드 명에 맞춰 지정할 수 있으며, 주석을 풀고 `sortString` 파라미터를
         * 추가하여 사용자가 원하는 필드로 정렬할 수 있습니다.
         *
         * @param page 페이지 번호 (기본값: 1)
         * @param size 페이지 크기 (기본값: 10)
//         * @param sortString 정렬할 기준
         * @return 정렬된 Pageable 객체
         */
        public static Pageable makePageable(Integer page, Integer size
//                                        ,String sortString
                                        ) {
            int pageNumber = Objects.requireNonNullElse(page, 1);
            int sizeNumber = Objects.requireNonNullElse(size, 10);
            String sort =
//                    Objects.requireNonNullElse(sortString,
                        "id"
//                    )
            ;
            return PageRequest.of(pageNumber - 1, sizeNumber, Sort.by(Sort.Order.desc(sort)));
        }
    }

    public static class editorImg {
        public static String updateImgSrc(String html, List<String> newSrcs) {
            // 정규식으로 <img> 태그의 src 값을 추출
            Pattern pattern = Pattern.compile("<img[^>]*src=['\"](blob:[^'\"]+)['\"][^>]*>");
            Matcher matcher = pattern.matcher(html);

            List<String> srcList = new ArrayList<>();

            // src 값을 srcList에 저장
            while (matcher.find()) {
                srcList.add(matcher.group(1));  // src 값만 저장
            }

            // 새로운 html
            StringBuilder updatedHtml = new StringBuilder(html);

            for (int i = 0; i < srcList.size(); i++) {
                // 찾은 src 값을 새로운 src 값으로 교체
                String oldSrc = srcList.get(i);
                String newSrc = newSrcs.get(i);
                int startIdx = updatedHtml.indexOf(oldSrc);

                if (startIdx != -1) {
                    updatedHtml.replace(startIdx, startIdx + oldSrc.length(), newSrc);
                }
            }

            return updatedHtml.toString();
        }
    }
}
