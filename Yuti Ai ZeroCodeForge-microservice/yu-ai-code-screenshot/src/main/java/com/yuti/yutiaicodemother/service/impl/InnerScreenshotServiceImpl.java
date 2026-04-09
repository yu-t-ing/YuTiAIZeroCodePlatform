package com.yuti.yutiaicodemother.service.impl;

import com.yuti.yutiaicodemother.innerservice.InnerScreenshotService;
import com.yuti.yutiaicodemother.service.ScreenshotService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class InnerScreenshotServiceImpl implements InnerScreenshotService {

    @Resource
    private ScreenshotService screenshotService;

    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        return screenshotService.generateAndUploadScreenshot(webUrl);
    }
}