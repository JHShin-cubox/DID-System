/*==================================================================
프로젝트명 : DID 시스템
작성지 : 신정호
작성일 : 2023년 12월 04일
용도 : DID 시스템 컨트롤러
==================================================================*/

package com.totalViewserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final SimpMessagingTemplate messagingTemplate;

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
        try {
            // 허용할 파일 타입
            String allowedFileType = "image/png";

            // 파일이 PNG 형식인지 확인
            if (!file.getContentType().equals(allowedFileType)) {
                // 허용하지 않는 파일 형식일 경우 처리
                result = "Fail - Only PNG files are allowed";
                return result;
            }

            // 파일을 업로드할 경로 설정
//            Path uploadDir = Path.of("C:/testImage");
            Path uploadDir = Path.of("/home/cubox/totalImage/");

            //폴더가 없을시 생성
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 파일 저장
            Path filePath = uploadDir.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 모델에 파일 경로 전달
            model.addAttribute("filePath", filePath.toString());
            result = "Success";
        } catch (Exception e) {
            e.printStackTrace();
            result = "Fail";
        }

        //웹 소켓에 던질 데이터 맵 형태로 압축
        Map<String, Object> message = new HashMap<>();
        message.put("filename", file.getOriginalFilename());
        message.put("status", status);

        //항목 값에 따라 웹소켓 통신
        if(type.equals("xray")) messagingTemplate.convertAndSend("/totalView/xray", message);
        else if(type.equals("shoe")) messagingTemplate.convertAndSend("/totalView/shoe", message);
        return result;
    }

    //파일 삭제 시스템
    @PostMapping("/imageDeleteCom")
    @ResponseBody
    public void deleteImage(@RequestParam("name")String name){
//        String deletePath = "C:/testImage";
        String deletePath = "/home/cubox/totalImage/";

        File directory = new File(deletePath);

        try {
            File fileToDelete = new File(directory, name);

            if (fileToDelete.exists() && fileToDelete.isFile()) {
                if (fileToDelete.delete()) {
                    System.out.println("파일 " + name + "이(가) 성공적으로 삭제되었습니다.");
                } else {
                    System.out.println("파일 " + name + " 삭제 실패");
                }
            } else {
                System.out.println("디렉토리 내에 파일 " + name + "이(가) 존재하지 않습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
