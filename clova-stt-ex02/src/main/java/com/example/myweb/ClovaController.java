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
            String postParams = "speaker=nsangdo&volume=0&speed=0&pitch=0&format=mp3&text=" + encodedText;
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
//    @PostMapping("/recognize")
//    public String recognizeSpeech(@RequestBody byte[] voiceData) {
//        String language = "Kor";        // 언어 코드 ( Kor, Jpn, Eng, Chn )
//        String url = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=" + language;
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        headers.set("X-NCP-APIGW-API-KEY-ID", CLIENT_ID);
//        headers.set("X-NCP-APIGW-API-KEY", API_KEY);
//
//        HttpEntity<byte[]> entity = new HttpEntity<>(voiceData, headers);
//        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
//
//        return response.getBody();
//    }

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
