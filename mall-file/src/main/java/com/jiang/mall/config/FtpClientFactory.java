/*
 * Copyright (c) 2024 Jiang RongJun
 * Jiang Mall is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 * PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.jiang.mall.config;

import com.jiang.mall.domain.config.FtpStorageConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FtpClientFactory {

    private final FtpStorageConfig ftpConfig;

    public FTPClient create() throws IOException {
        FTPClient ftp = new FTPClient();
        ftp.connect(ftpConfig.getHost(), ftpConfig.getPort());
        ftp.login(ftpConfig.getUsername(), ftpConfig.getPassword());
        ftp.enterLocalPassiveMode();
        return ftp;
    }

    public boolean validate(@NotNull FTPClient ftp) {
        try {
            return ftp.sendNoOp();
        } catch (IOException e) {
            return false;
        }
    }

    public void destroy(@NotNull FTPClient ftp) {
        try {
            if (ftp.isConnected()) {
                ftp.logout();
                ftp.disconnect();
            }
        } catch (IOException e) {
//            log.error("FTP连接销毁异常", e);
        }
    }
}