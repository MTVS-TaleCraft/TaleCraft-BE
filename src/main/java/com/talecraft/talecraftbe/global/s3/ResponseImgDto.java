package com.talecraft.talecraftbe.global.s3;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseImgDto {
    String presignedUrl;
    String publicUrl;
}
