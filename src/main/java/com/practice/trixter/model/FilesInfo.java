package com.practice.trixter.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilesInfo {
    private String id;
    private String url;
    private String name;
    private Long size;
}
