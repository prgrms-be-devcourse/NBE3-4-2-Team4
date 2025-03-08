package com.NBE3_4_2_Team4.standard.util

import com.NBE3_4_2_Team4.global.config.AppConfig
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.imageio.ImageIO

class Ut {
    object str {
        fun isBlank(str: String?): Boolean {
            return str == null || str.trim().isEmpty()
        }

        @JvmStatic
        fun lcfirst(str: String): String {
            return str[0].lowercaseChar() + str.substring(1)
        }
    }

    object file {
        private const val ORIGINAL_FILE_NAME_SEPARATOR = "--originalFileName_"
        const val META_STR_SEPARATOR = "_metaStr--"

        private val MIME_TYPE_MAP: LinkedHashMap<String, String> = linkedMapOf(
            "application/json" to "json",
            "text/plain" to "txt",
            "text/html" to "html",
            "text/css" to "css",
            "application/javascript" to "js",
            "image/jpeg" to "jpg",
            "image/png" to "png",
            "image/gif" to "gif",
            "image/webp" to "webp",
            "image/svg+xml" to "svg",
            "application/pdf" to "pdf",
            "application/xml" to "xml",
            "application/zip" to "zip",
            "application/gzip" to "gz",
            "application/x-tar" to "tar",
            "application/x-7z-compressed" to "7z",
            "application/vnd.rar" to "rar",
            "audio/mpeg" to "mp3",
            "audio/mp4" to "m4a",
            "audio/x-m4a" to "m4a",
            "audio/wav" to "wav",
            "video/quicktime" to "mov",
            "video/mp4" to "mp4",
            "video/webm" to "webm",
            "video/x-msvideo" to "avi"
        )

        @JvmStatic
        @JvmOverloads
        fun downloadByHttp(url: String, dirPath: String, uniqueFilename: Boolean = true): String {
            val client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build()

            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build()

            val tempFilePath = "$dirPath/${UUID.randomUUID()}.tmp"

            mkdir(dirPath)

            // 실제 파일 다운로드
            val response = client.send(
                request,
                HttpResponse.BodyHandlers.ofFile(Path.of(tempFilePath))
            )

            // 파일 확장자 추출
            var extension = getExtensionFromResponse(response)

            if (extension == "tmp") extension = getExtensionByTika(tempFilePath)

            // 파일명 추출
            var filename = getFilenameWithoutExtFromUrl(url)

            filename = if (uniqueFilename)
                "${UUID.randomUUID()}$ORIGINAL_FILE_NAME_SEPARATOR$filename"
            else
                filename

            val newFilePath = "$dirPath/$filename.$extension"

            mv(tempFilePath, newFilePath)

            return newFilePath
        }

        @JvmStatic
        fun getExtensionByTika(filePath: String): String {
            val mineType = AppConfig.getTika().detect(filePath)

            return MIME_TYPE_MAP.getOrDefault(mineType, "tmp")
        }

        @JvmStatic
        fun mv(oldFilePath: String, newFilePath: String) {
            mkdir(Paths.get(newFilePath).parent.toString())

            Files.move(
                Path.of(oldFilePath),
                Path.of(newFilePath),
                StandardCopyOption.REPLACE_EXISTING
            )
        }

        @JvmStatic
        private fun mkdir(dirPath: String) {
            val path = Path.of(dirPath)

            if (Files.exists(path)) return

            Files.createDirectories(path)
        }

        private fun getFilenameWithoutExtFromUrl(url: String): String {
            return try {
                val path = URI(url).path
                val filename = Path.of(path).fileName.toString()
                // 확장자 제거
                if (filename.contains("."))
                    filename.substring(0, filename.lastIndexOf('.'))
                else
                    filename
            } catch (e: URISyntaxException) {
                // URL에서 파일명을 추출할 수 없는 경우 타임스탬프 사용
                "download_${System.currentTimeMillis()}"
            }
        }

        private fun getExtensionFromResponse(response: HttpResponse<*>): String {
            return response.headers()
                .firstValue("Content-Type")
                .map { MIME_TYPE_MAP.getOrDefault(it, "tmp") }
                .orElse("tmp")
        }

        @JvmStatic
        fun delete(filePath: String) {
            Files.deleteIfExists(Path.of(filePath))
        }

        @JvmStatic
        fun getOriginalFileName(filePath: String): String {
            val originalFileName = Path.of(filePath).fileName.toString()

            return if (originalFileName.contains(ORIGINAL_FILE_NAME_SEPARATOR))
                originalFileName.substring(originalFileName.indexOf(ORIGINAL_FILE_NAME_SEPARATOR) + ORIGINAL_FILE_NAME_SEPARATOR.length)
            else
                originalFileName
        }

        @JvmStatic
        fun getFileExt(filePath: String): String {
            val filename = getOriginalFileName(filePath)

            return if (filename.contains("."))
                filename.substring(filename.lastIndexOf('.') + 1)
            else
                ""
        }

        @JvmStatic
        fun getFileSize(filePath: String): Int {
            return Files.size(Path.of(filePath)).toInt()
        }

        @JvmStatic
        fun rm(filePath: String) {
            val path = Path.of(filePath)

            if (!Files.exists(path)) return

            if (Files.isRegularFile(path)) {
                // 파일이면 바로 삭제
                Files.delete(path)
            } else {
                // 디렉터리면 내부 파일들 삭제 후 디렉터리 삭제
                Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
                    @Throws(IOException::class)
                    override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                        Files.delete(file)
                        return FileVisitResult.CONTINUE
                    }

                    @Throws(IOException::class)
                    override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
                        Files.delete(dir)
                        return FileVisitResult.CONTINUE
                    }
                })
            }
        }

        @JvmStatic
        fun getFileExtTypeCodeFromFileExt(ext: String): String {
            return when (ext) {
                "jpeg", "jpg", "gif", "png", "svg", "webp" -> "img"
                "mp4", "avi", "mov" -> "video"
                "mp3", "m4a" -> "audio"
                else -> "etc"
            }
        }

        @JvmStatic
        fun getFileExtType2CodeFromFileExt(ext: String): String {
            return when (ext) {
                "jpeg", "jpg" -> "jpg"
                else -> ext
            }
        }

        @JvmStatic
        fun getMetadata(filePath: String): Map<String, Any> {
            val ext = getFileExt(filePath)
            val fileExtTypeCode = getFileExtTypeCodeFromFileExt(ext)

            return if (fileExtTypeCode == "img") getImgMetadata(filePath) else emptyMap()
        }

        private fun getImgMetadata(filePath: String): Map<String, Any> {
            val metadata = java.util.LinkedHashMap<String, Any>()

            try {
                ImageIO.createImageInputStream(File(filePath)).use { input ->
                    val readers = ImageIO.getImageReaders(input)

                    if (!readers.hasNext()) {
                        throw IOException("지원되지 않는 이미지 형식: $filePath")
                    }

                    val reader = readers.next()
                    reader.input = input

                    val width = reader.getWidth(0)
                    val height = reader.getHeight(0)

                    metadata["width"] = width
                    metadata["height"] = height

                    reader.dispose()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return metadata
        }

        @JvmStatic
        @JvmOverloads
        fun toFile(multipartFile: MultipartFile?, dirPath: String, metaStr: String = ""): String {
            if (multipartFile == null) return ""
            if (multipartFile.isEmpty) return ""

            val fileName = if (str.isBlank(metaStr))
                "${UUID.randomUUID()}$ORIGINAL_FILE_NAME_SEPARATOR${multipartFile.originalFilename}"
            else
                "$metaStr$META_STR_SEPARATOR${UUID.randomUUID()}$ORIGINAL_FILE_NAME_SEPARATOR${multipartFile.originalFilename}"

            val filePath = "$dirPath/$fileName"

            mkdir(dirPath)
            multipartFile.transferTo(File(filePath))

            return filePath
        }

        @JvmStatic
        fun copy(filePath: String, newFilePath: String) {
            mkdir(Paths.get(newFilePath).parent.toString())

            Files.copy(
                Path.of(filePath),
                Path.of(newFilePath),
                StandardCopyOption.REPLACE_EXISTING
            )
        }

        @JvmStatic
        fun getContentType(fileExt: String): String {
            return MIME_TYPE_MAP.entries
                .find { it.value == fileExt }
                ?.key ?: ""
        }

        @JvmStatic
        fun withNewExt(fileName: String, fileExt: String): String {
            return if (fileName.contains("."))
                fileName.substring(0, fileName.lastIndexOf('.') + 1) + fileExt
            else
                "$fileName.$fileExt"
        }
    }

    object cmd {
        @JvmStatic
        fun runAsync(cmd: String) {
            Thread {
                run(cmd)
            }.start()
        }

        fun run(cmd: String) {
            try {
                val os = System.getProperty("os.name").lowercase(Locale.getDefault())

                val processBuilder = if (os.contains("win")) {
                    // Windows 시스템에서는 Git Bash 경로 사용
                    ProcessBuilder("C:\\Program Files\\Git\\bin\\bash.exe", "-c", cmd)
                } else {
                    // macOS 또는 Linux 시스템에서는 bash 사용
                    ProcessBuilder("bash", "-c", cmd)
                }

                val process = processBuilder.start()

                val reader = BufferedReader(InputStreamReader(process.inputStream))
                var line: String?

                // 프로세스의 출력 읽기
                while ((reader.readLine().also { line = it }) != null) {
                    println(line) // IntelliJ 콘솔에 출력
                }

                // 에러 출력 읽기
                val errorReader = BufferedReader(InputStreamReader(process.errorStream))
                while ((errorReader.readLine().also { line = it }) != null) {
                    System.err.println(line) // 에러 메시지를 IntelliJ 콘솔에 출력
                }

                process.waitFor(1, TimeUnit.MINUTES)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    object date {
        @JvmStatic
        fun getCurrentDateFormatted(pattern: String): String {
            val simpleDateFormat = SimpleDateFormat(pattern)
            return simpleDateFormat.format(Date())
        }
    }

    object url {
        @JvmStatic
        fun encode(str: String): String {
            return try {
                URLEncoder.encode(str, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                str
            }
        }

        @JvmStatic
        fun removeDomain(url: String): String {
            return url.replaceFirst("https?://[^/]+".toRegex(), "")
        }
    }

    object pageable {
        /**
         * Pageable 객체를 생성하는 유틸리티 클래스입니다.
         * 기본적으로 페이지 번호는 1, 페이지 크기는 10, 정렬 기준은 "id"로 설정됩니다.
         *
         * 정렬 기준은 엔티티의 필드 명에 맞춰 지정할 수 있으며, 주석을 풀고 `sortString` 파라미터를
         * 추가하여 사용자가 원하는 필드로 정렬할 수 있습니다.
         *
         * @param page 페이지 번호 (기본값: 1)
         * @param size 페이지 크기 (기본값: 10)
         * //         * @param sortString 정렬할 기준
         * @return 정렬된 Pageable 객체
         */
        @JvmStatic
        fun makePageable(
            page: Int, size: Int // ,sortString: String
        ): Pageable {
            val pageNumber = Objects.requireNonNullElse(page, 1)
            val sizeNumber = Objects.requireNonNullElse(size, 10)
            val sort =  // Objects.requireNonNullElse(sortString,
                "id"
            // )

            return PageRequest.of(pageNumber - 1, sizeNumber, Sort.by(Sort.Order.desc(sort)))
        }
    }

    object editorImg {
        @JvmStatic
        fun updateImgSrc(html: String, newSrcs: List<String>): String {
            // 정규식으로 <img> 태그의 src 값을 추출
            val pattern = Pattern.compile("<img[^>]*src=['\"](blob:[^'\"]+)['\"][^>]*>")
            val matcher = pattern.matcher(html)

            val srcList: MutableList<String> = mutableListOf()

            // src 값을 srcList에 저장
            while (matcher.find()) {
                srcList.add(matcher.group(1)) // src 값만 저장
            }

            // 새로운 html
            val updatedHtml = StringBuilder(html)

            for (i in srcList.indices) {
                // 찾은 src 값을 새로운 src 값으로 교체
                val oldSrc = srcList[i]
                val newSrc = newSrcs[i]
                val startIdx = updatedHtml.indexOf(oldSrc)

                if (startIdx != -1) {
                    updatedHtml.replace(startIdx, startIdx + oldSrc.length, newSrc)
                }
            }

            return updatedHtml.toString()
        }
    }
}
