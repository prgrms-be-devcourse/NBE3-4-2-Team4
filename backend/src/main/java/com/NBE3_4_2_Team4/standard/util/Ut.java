package com.NBE3_4_2_Team4.standard.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Ut {
    public static class str {
        public static boolean isBlank(String str) {
            return str == null || str.trim().isEmpty();
        }
    }

    public static class file {
        public static void downloadByHttp(String url, String dirPath) {
            try {
                HttpClient client = HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                // 먼저 헤더만 가져오기 위한 HEAD 요청
                HttpResponse<Void> headResponse = client.send(
                        HttpRequest.newBuilder(URI.create(url))
                                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                .build(),
                        HttpResponse.BodyHandlers.discarding()
                );

                // 실제 파일 다운로드
                HttpResponse<Path> response = client.send(request,
                        HttpResponse.BodyHandlers.ofFile(
                                createTargetPath(url, dirPath, headResponse)
                        ));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("다운로드 중 오류 발생: " + e.getMessage(), e);
            }
        }

        private static Path createTargetPath(String url, String dirPath, HttpResponse<?> response) {
            // 디렉토리가 없으면 생성
            Path directory = Path.of(dirPath);
            if (!Files.exists(directory)) {
                try {
                    Files.createDirectories(directory);
                } catch (IOException e) {
                    throw new RuntimeException("디렉토리 생성 실패: " + e.getMessage(), e);
                }
            }

            // 파일명 생성
            String filename = getFilenameFromUrl(url);
            String extension = getExtensionFromResponse(response);

            return directory.resolve(filename + extension);
        }

        private static String getFilenameFromUrl(String url) {
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
                    .map(contentType -> {
                        // MIME 타입에 따른 확장자 매핑
                        return switch (contentType.split(";")[0].trim().toLowerCase()) {
                            case "application/json" -> ".json";
                            case "text/plain" -> ".txt";
                            case "text/html" -> ".html";
                            case "image/jpeg" -> ".jpg";
                            case "image/png" -> ".png";
                            case "application/pdf" -> ".pdf";
                            case "application/xml" -> ".xml";
                            case "application/zip" -> ".zip";
                            default -> "";
                        };
                    })
                    .orElse("");
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
        public static Pageable pageable(Integer page, Integer size
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
}
