/*==================================================================
프로젝트명 : DID 시스템
작성지 : 신정호
작성일 : 2023년 12월 04일
수정일 : 2023년 12월 15일
용도 : DID 시스템 컨트롤러
변경내용 :
 - 허용 확장자는 png 확장자만 가능하였지만 jpg, bmp 확장자도 허용함
==================================================================*/

package com.totalViewserver.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final SimpMessagingTemplate messagingTemplate;
    private final Logger log = LoggerFactory.getLogger(getClass());


    @GetMapping(value = "/xray")
    public String XrayBoard(Model model){
        return "xray";
    }

    @GetMapping(value = "/shoe")
    public String ShoeBoard(Model model){
        return "shoe";
    }


    //업로드 시스템
    @PostMapping("/upload")
    @ResponseBody
    public String sendMessage(@RequestPart("file") MultipartFile file, @RequestParam("type")String type, @RequestParam("status")String status, Model model) {
        String result;
//        String imagePath = "C:/did_image";
        String imagePath = "/home/cubox/totalImage/";
        String originalFilename = file.getOriginalFilename();

        //마지막 .위치 찾기
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        try {
            String allowedFileTypes = "png,jpg,jpeg,bmp";

            // 파일이 허용된 형식인지 확인

            if (!Arrays.asList(allowedFileTypes.split(",")).contains(fileExtension)) {
                // 파일 확장자가 허용되지 않으면 에러 처리
                log.warn("DiD System Log: File type not allowed - {}", originalFilename);
                return "File type not allowed";
            }



            // 파일을 업로드할 경로 설정
            Path uploadDir = Path.of(imagePath);

            //폴더가 없을시 생성
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            // 파일 저장
            Path filePath = uploadDir.resolve(originalFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 모델에 파일 경로 전달
            model.addAttribute("filePath", filePath.toString());
            result = "Success";
        } catch (Exception e) {
            e.printStackTrace();
            result = "Fail";
        }

        LocalDateTime now = LocalDateTime.now();
        String formatDate = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

//        String fileType = file.getOriginalFilename().trim().equalsIgnoreCase();/z

        //웹 소켓에 던질 데이터 맵 형태로 압축
        Map<String, Object> message = new HashMap<>();
        message.put("filename", originalFilename);
//        message.put("fileType",fileType);
        message.put("status", status);

        //항목 값에 따라 웹소켓 통신
        if(type.equals("xray")) messagingTemplate.convertAndSend("/totalView/xray", message);
        else if(type.equals("shoe")) messagingTemplate.convertAndSend("/totalView/shoe", message);
        message.put("type",type);
        message.put("result",result);
        message.put("reg_date",formatDate);
        log.warn("DiD System Log: {}",message);

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            File directory = new File(imagePath);
            File fileToDelete = new File(directory, file.getOriginalFilename());
            if (fileToDelete.exists()) {
                boolean deleted = fileToDelete.delete();
                if (deleted) {
                    log.warn("File has been deleted successfully: {}", fileToDelete.getAbsolutePath());
                } else {
                    log.warn("File Delete Failed 삭제 실패: {}", fileToDelete.getAbsolutePath());
                }
            }
        }, 1, TimeUnit.SECONDS);

        return result;
    }

}
