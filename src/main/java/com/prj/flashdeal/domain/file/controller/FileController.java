package com.prj.flashdeal.domain.file.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prj.flashdeal.domain.file.dto.response.FileUploadResponse;
import com.prj.flashdeal.domain.file.service.FileService;
import com.prj.flashdeal.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
        @RequestParam("file") MultipartFile file
    ) {
        String url = fileService.uploadFile(file);
        return ApiResponse.success(HttpStatus.OK, "파일이 업로드되었습니다.", new FileUploadResponse(url));
    }
}
