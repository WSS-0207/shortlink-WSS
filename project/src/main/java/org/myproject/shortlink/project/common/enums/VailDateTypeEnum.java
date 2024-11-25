package org.myproject.shortlink.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VailDateTypeEnum {
    PERMANERNT(0),
    CUSTOM(1);

    @Getter
    private final int type;
}
