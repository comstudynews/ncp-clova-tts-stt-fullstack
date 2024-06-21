# NCP CLOVA (tts, stt) 구현

- 프론트엔드 화면
- 텍스트를 음성으로, 음성을 텍스트로 변환 하는 기술 인공지능 API 활용.

![Untitled](NCP%20CLOVA%20(tts,%20stt)%20%E1%84%80%E1%85%AE%E1%84%92%E1%85%A7%E1%86%AB%208d24d3efdd0341c3a321bad0989ef823/Untitled.png)

# 참고 문서

### Application 사용 가이드

- [https://guide.ncloud-docs.com/docs/naveropenapiv3-application](https://guide.ncloud-docs.com/docs/naveropenapiv3-application)

### tts(Preminu) 가이드

- CLOVA Voice
- tts는 CLOVA Voice - Premium API 서비스입니다.
- [https://api.ncloud-docs.com/docs/ai-naver-clovavoice-ttspremium](https://api.ncloud-docs.com/docs/ai-naver-clovavoice-ttspremium)

### stt(Speech-To-Text) 가이드

- CLOVA Speech Recognition(CSR)
- [https://api.ncloud-docs.com/docs/ai-naver-clovaspeechrecognition-stt](https://api.ncloud-docs.com/docs/ai-naver-clovaspeechrecognition-stt)

# NCC Application 등록 및 인증키 발급

### Application 이름 설정 및 Service 선택

- **주의**: CLOVA Voice-Preminum은 기본료 월 90,000원 (tts 서비스를 위해서 필요)

![Untitled](NCP%20CLOVA%20(tts,%20stt)%20%E1%84%80%E1%85%AE%E1%84%92%E1%85%A7%E1%86%AB%208d24d3efdd0341c3a321bad0989ef823/Untitled%201.png)

### 서비스 환경 등록

- (선택) CLOVA Speech Recognition (CSR) 선택 시 Android 앱 패키지 이름 또는 iOS Bundle ID를 입력해야 합니다. 입력하지 않는 경우, SDK 이용에 일부 제약이 있을 수 있습니다.

![Untitled](NCP%20CLOVA%20(tts,%20stt)%20%E1%84%80%E1%85%AE%E1%84%92%E1%85%A7%E1%86%AB%208d24d3efdd0341c3a321bad0989ef823/Untitled%202.png)

### 인증 정보 확인

![Untitled](NCP%20CLOVA%20(tts,%20stt)%20%E1%84%80%E1%85%AE%E1%84%92%E1%85%A7%E1%86%AB%208d24d3efdd0341c3a321bad0989ef823/Untitled%203.png)

### 인증 정보

![Untitled](NCP%20CLOVA%20(tts,%20stt)%20%E1%84%80%E1%85%AE%E1%84%92%E1%85%A7%E1%86%AB%208d24d3efdd0341c3a321bad0989ef823/Untitled%204.png)

# 1. 백엔드 구현

- STS4
- Spring Boot 3
- Maven

![Untitled](NCP%20CLOVA%20(tts,%20stt)%20%E1%84%80%E1%85%AE%E1%84%92%E1%85%A7%E1%86%AB%208d24d3efdd0341c3a321bad0989ef823/Untitled%205.png)

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.3.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>clova-ex</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>clova-stt-ex02</name>
	<description>clova stt for Spring Boot</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
		    <groupId>org.springframework.boot</groupId>
		    <artifactId>spring-boot-starter-web</artifactId>		    
		</dependency>
		
		<dependency>
		    <groupId>org.springframework.boot</groupId>
		    <artifactId>spring-boot-starter-tomcat</artifactId>		    
		</dependency>
		
		<dependency>
		    <groupId>com.fasterxml.jackson.core</groupId>
		    <artifactId>jackson-core</artifactId>
		</dependency>
		
		<dependency>
		    <groupId>com.fasterxml.jackson.core</groupId>
		    <artifactId>jackson-databind</artifactId>
		</dependency>
	</dependencies>

	<build>
	    <resources>
	        <resource>
	            <directory>src/main/resources</directory>
	            <filtering>true</filtering>
	        </resource>
	    </resources>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

### application.properties

```xml
spring.application.name=clova-stt-ex02

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### WebConfig.java

- CORS 설정 및 업로드 파일 최대 크기 설정

```java
package com.example.myweb;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	registry.addMapping("/**")
	        .allowedOrigins("http://localhost:3000")
	        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
	        .allowedHeaders("*")
	        .allowCredentials(true);
    }
    
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Set maximum file size
        factory.setMaxFileSize(DataSize.ofMegabytes(10)); // 10MB
        // Set maximum request size
        factory.setMaxRequestSize(DataSize.ofMegabytes(10)); // 10MB
        return factory.createMultipartConfig();
    }
}
```

### ClovaController.java

```java
package com.example.myweb;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class ClovaController {

    public static final String API_KEY = "API_KEY";
    public static final String CLIENT_ID = "CLIENT_ID";

    // 음성 합성(TTS)
    @PostMapping("/synthesize")
    public ResponseEntity<byte[]> synthesizeSpeech(@RequestBody Map<String, String> requestBody) {
        String clientId = CLIENT_ID;
        String clientSecret = API_KEY;
        String text = requestBody.get("text");

        try {
            String encodedText = URLEncoder.encode(text, "UTF-8"); // 입력된 텍스트를 인코딩
            String apiURL = "https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // post request
            String postParams = "speaker=nara&volume=0&speed=0&pitch=0&format=mp3&text=" + encodedText;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            if (responseCode == 200) { // 정상 호출
                InputStream is = con.getInputStream();
                byte[] bytes = is.readAllBytes();
                is.close();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentLength(bytes.length);
                
                System.out.println(new ResponseEntity<>(bytes, headers, HttpStatus.OK));
                
                return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
            } else {  // 오류 발생
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 음성 인식(STT)
    @PostMapping("/recognize")
    public String recognizeSpeech(@RequestBody byte[] voiceData) {
        String language = "Kor";        // 언어 코드 ( Kor, Jpn, Eng, Chn )
        String url = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=" + language;
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
        headers.set("X-NCP-APIGW-API-KEY", API_KEY);

        HttpEntity<byte[]> entity = new HttpEntity<>(voiceData, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        return response.getBody();
    }

    // STT 음성인식
    @PostMapping("/fileUpload")
    public String fileUpload(@RequestParam("upload") MultipartFile upload, HttpServletRequest req) {
        System.out.println("NaverController STT " + new Date());

        // 음성파일을 업로드할 경로
        String uploadpath = req.getServletContext().getRealPath("/");
        System.out.println("uploadpath >> " + uploadpath);

        String filename = upload.getOriginalFilename();
        String filepath = uploadpath + "/" + filename;

        try {
            // 파일 저장
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            os.write(upload.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }

        // Naver Cloud AI
        String resp = NaverCloud.stt(filepath);
        return resp;
    }
}

```

### NaverCloud.java

- 음성 변환 기능을 Controller에서 분리한 Command

```java
package com.example.myweb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NaverCloud {
	public static String stt(String filepath) {	// 컨트롤러에서 사용하기 위해 static으로
		
		String clientId = ClovaController.CLIENT_ID;             // Application Client ID
        String clientSecret = ClovaController.API_KEY;     	  // Application Client Secret
        StringBuffer response = new StringBuffer();	// 반환하기 위해 try문 외부에서 선언

        try {
            //String imgFile = "C:\\Users\\beomj\\Downloads\\exSound.wav";
            
            File voiceFile = new File(filepath);

            String language = "Kor";        // 언어 코드 ( Kor, Jpn, Eng, Chn )
            String apiURL = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=" + language;
            URL url = new URL(apiURL);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            OutputStream outputStream = conn.getOutputStream();
            FileInputStream inputStream = new FileInputStream(voiceFile);
            //InputStream inputStream = this.getClass().getResourceAsStream(imgFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();
            BufferedReader br = null;
            int responseCode = conn.getResponseCode();
            if(responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {  // 오류 발생
                System.out.println("error!!!!!!! responseCode= " + responseCode);
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }
            String inputLine;

            if(br != null) {
                response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
                return response.toString();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
		return "no!";
	}
}

```

### FileUploadExceptionAdvice

- 파일 업로드 예외 처리 클래스

```java
package com.example.myweb;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class FileUploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "File too large!");
        return "redirect:/uploadStatus";
    }
}
```

### 실행 결과 및 Post 요청 결과

```bash
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

[32m :: Spring Boot :: [39m              [2m (v3.3.0)[0;39m

[2m2024-06-20T19:03:19.435+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36mc.example.myweb.ClovaSttEx02Application [0;39m [2m:[0;39m Starting ClovaSttEx02Application using Java 17.0.7 with PID 31420 (D:\[멀티캠퍼스]캠퍼스세븐\2024-05-20\sts4-work2024\clova-stt-ex02\target\classes started by beomj in D:\[멀티캠퍼스]캠퍼스세븐\2024-05-20\sts4-work2024\clova-stt-ex02)
[2m2024-06-20T19:03:19.438+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36mc.example.myweb.ClovaSttEx02Application [0;39m [2m:[0;39m No active profile set, falling back to 1 default profile: "default"
[2m2024-06-20T19:03:19.473+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36m.e.DevToolsPropertyDefaultsPostProcessor[0;39m [2m:[0;39m Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
[2m2024-06-20T19:03:19.473+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36m.e.DevToolsPropertyDefaultsPostProcessor[0;39m [2m:[0;39m For additional web related logging consider setting the 'logging.level.web' property to 'DEBUG'
[2m2024-06-20T19:03:20.110+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36mo.s.b.w.embedded.tomcat.TomcatWebServer [0;39m [2m:[0;39m Tomcat initialized with port 8080 (http)
[2m2024-06-20T19:03:20.118+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36mo.apache.catalina.core.StandardService  [0;39m [2m:[0;39m Starting service [Tomcat]
[2m2024-06-20T19:03:20.119+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36mo.apache.catalina.core.StandardEngine   [0;39m [2m:[0;39m Starting Servlet engine: [Apache Tomcat/10.1.24]
[2m2024-06-20T19:03:20.166+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36mo.a.c.c.C.[Tomcat].[localhost].[/]      [0;39m [2m:[0;39m Initializing Spring embedded WebApplicationContext
[2m2024-06-20T19:03:20.166+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36mw.s.c.ServletWebServerApplicationContext[0;39m [2m:[0;39m Root WebApplicationContext: initialization completed in 693 ms
[2m2024-06-20T19:03:20.400+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36mo.s.b.d.a.OptionalLiveReloadServer      [0;39m [2m:[0;39m LiveReload server is running on port 35729
[2m2024-06-20T19:03:20.425+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36mo.s.b.w.embedded.tomcat.TomcatWebServer [0;39m [2m:[0;39m Tomcat started on port 8080 (http) with context path '/'
[2m2024-06-20T19:03:20.431+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [  restartedMain][0;39m [2m[0;39m[36mc.example.myweb.ClovaSttEx02Application [0;39m [2m:[0;39m Started ClovaSttEx02Application in 1.255 seconds (process running for 1.912)
[2m2024-06-20T19:03:30.556+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [nio-8080-exec-1][0;39m [2m[0;39m[36mo.a.c.c.C.[Tomcat].[localhost].[/]      [0;39m [2m:[0;39m Initializing Spring DispatcherServlet 'dispatcherServlet'
[2m2024-06-20T19:03:30.556+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [nio-8080-exec-1][0;39m [2m[0;39m[36mo.s.web.servlet.DispatcherServlet       [0;39m [2m:[0;39m Initializing Servlet 'dispatcherServlet'
[2m2024-06-20T19:03:30.557+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [nio-8080-exec-1][0;39m [2m[0;39m[36mo.s.web.servlet.DispatcherServlet       [0;39m [2m:[0;39m Completed initialization in 1 ms
<200 OK OK,[B@42098d2,[Content-Type:"application/octet-stream", Content-Length:"6948"]>
<200 OK OK,[B@4acf6176,[Content-Type:"application/octet-stream", Content-Length:"6948"]>
NaverController STT Thu Jun 20 19:14:00 KST 2024
uploadpath >> C:\Users\beomj\AppData\Local\Temp\tomcat-docbase.8080.463685679882951686\
{"text":"여러분 안녕하세요 저는 김범준입니다 여기는 대한민국입니다 지금 녹음 기능을 테스트하고 있습니다 반갑습니다 행복하세요"}
[2m2024-06-20T19:44:39.030+09:00[0;39m [32m INFO[0;39m [35m31420[0;39m [2m---[0;39m [2m[clova-stt-ex02] [n(50)-127.0.0.1][0;39m [2m[0;39m[36minMXBeanRegistrar$SpringApplicationAdmin[0;39m [2m:[0;39m Application shutdown requested.

```

### PostMan을 이용한 테스트

- 프론트 엔드 구현 전에 먼저 PostMan으로 요청 테스트 합니다.
- Params 설정을 Body > form-data로 설정 하면 Key 속성에 **`File / Text`** 선택 옵션이 나타납니다. **`File`**로 선택하면 Value 속성에 파일 선택 버튼이 생성 됩니다.

![Untitled](NCP%20CLOVA%20(tts,%20stt)%20%E1%84%80%E1%85%AE%E1%84%92%E1%85%A7%E1%86%AB%208d24d3efdd0341c3a321bad0989ef823/Untitled%206.png)

# 2. 프론트 엔드 구현

- npx create-react-app frontend
- cd frontend
- npm start
- npm run build
- npm i -g serve
- serve -s build

![Untitled](NCP%20CLOVA%20(tts,%20stt)%20%E1%84%80%E1%85%AE%E1%84%92%E1%85%A7%E1%86%AB%208d24d3efdd0341c3a321bad0989ef823/Untitled%207.png)

### 의존성 추가

- Ajax 처리를 위한 모듈과 음성 인식을 위한 모듈을 추가 합니다.
- 버전이 안 맞아서 설치가 되지 않으면 --legacy-peer-deps 옵션을 주어 강제로 설치 합니다.

```bash
npm install axios
npm install react-native@latest @react-native-voice/voice@latest --legacy-peer-deps
```

### package.json

- 음성 처리를 위한 의존성 모듈이 추가 되었습니다.

```json
{
  "name": "frontend",
  "version": "0.1.0",
  "private": true,
  "dependencies": {
    "@react-native-voice/voice": "^3.2.4",
    "@testing-library/jest-dom": "^5.17.0",
    "@testing-library/react": "^13.4.0",
    "@testing-library/user-event": "^13.5.0",
    "axios": "^1.7.2",
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-native": "^0.74.2",
    "react-scripts": "5.0.1",
    "web-vitals": "^2.1.4"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build",
    "test": "react-scripts test",
    "eject": "react-scripts eject"
  },
  "eslintConfig": {
    "extends": [
      "react-app",
      "react-app/jest"
    ]
  },
  "browserslist": {
    "production": [
      ">0.2%",
      "not dead",
      "not op_mini all"
    ],
    "development": [
      "last 1 chrome version",
      "last 1 firefox version",
      "last 1 safari version"
    ]
  }
}
```

### SpeechComponent.js

- axios를 이용해서 Spring Boot 서버와 접속 합니다.
- 첫 번째 영역엔 텍스트를 입력하면 서버에서 음성으로 바꿔주는 기능입니다.
- 두 번째 영역은 음성을 넣으면 서버에서 텍스트로 바꿔주는 기능입니다.

```jsx
import React, { useState } from 'react';
import axios from 'axios';

function SpeechComponent() {
    const [text, setText] = useState('');
    const [audioBlob, setAudioBlob] = useState(null);

    const handleSynthesize = async () => {
        try {
            const response = await axios.post('http://localhost:8080/synthesize', { text }, {
                responseType: 'arraybuffer' // 바이너리 데이터를 받을 수 있도록 설정
            });
            const audioBlob = new Blob([response.data], { type: 'audio/mp3' });
            const audioUrl = URL.createObjectURL(audioBlob);
            const audio = new Audio(audioUrl);
            audio.play();
        } catch (error) {
            console.error('Error during synthesis:', error);
            alert('Error during synthesis: ' + error.message);
        }
    };

    const handleRecognize = async () => {
        if (!audioBlob) {
            alert('Please select an audio file first');
            return;
        }

        const formData = new FormData();
        formData.append('upload', audioBlob);

        try {
            const response = await axios.post('http://localhost:8080/fileUpload', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            console.log('Recognition result: ', response.data);
        } catch (error) {
            console.error('Error during recognition:', error);
            alert('Error during recognition: ' + error.message);
        }
    };

    return (
        <div>
            <h2>Clova TTS Test</h2>
            <input type="text" value={text} onChange={e => setText(e.target.value)} /><br/>
            <button onClick={handleSynthesize}>Convert to Speech</button>
            <hr/>
            <h2>Clova STT Test</h2>
            <input type="file" onChange={e => setAudioBlob(e.target.files[0])} /><br/>
            <button onClick={handleRecognize}>Recognize Speech</button>
        </div>
    );
}

export default SpeechComponent;
```