package com.yuti.yutiaicodemother.config;

import com.yuti.yutiaicodemother.constant.AppConstant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源配置
 * 用于将生成的代码文件映射到 HTTP 路径，供前端预览使用
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将生成的代码文件映射到 /api/static/** 路径
        // 这样前端可以通过 http://localhost:8123/api/static/html_123/index.html 访问生成的网页

        // 代码输出目录映射 - 使用绝对路径
        String codeOutputDir = AppConstant.CODE_OUTPUT_ROOT_DIR;
        // 确保路径以斜杠结尾
        if (!codeOutputDir.endsWith("/") && !codeOutputDir.endsWith("\\")) {
            codeOutputDir += "/";
        }
        // 转换为绝对路径并添加 file: 前缀
        String absolutePath = new java.io.File(codeOutputDir).getAbsolutePath();
        String resourceLocation = "file:" + absolutePath + "/";

        registry.addResourceHandler("/api/static/**")
                .addResourceLocations(resourceLocation)
                .setCachePeriod(0); // 禁用缓存，方便开发调试

        System.out.println("✅ 静态资源映射已配置：");
        System.out.println("   HTTP 路径: /api/static/**");
        System.out.println("   本地目录: " + resourceLocation);
        System.out.println("   绝对路径: " + absolutePath);
    }
}
