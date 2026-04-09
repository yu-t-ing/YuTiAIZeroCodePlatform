package com.yuti.yutiaicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import com.yuti.yutiaicodemother.ai.model.HtmlCodeResult;
import com.yuti.yutiaicodemother.exception.BusinessException;
import com.yuti.yutiaicodemother.exception.ErrorCode;
import com.yuti.yutiaicodemother.model.enums.CodeGenTypeEnum;

/**
 * HTML代码文件保存器
 *
 * @author 姜瓷
 */
public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        // HTML 代码不能为空
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML 代码不能为空");
        }
    }
}
