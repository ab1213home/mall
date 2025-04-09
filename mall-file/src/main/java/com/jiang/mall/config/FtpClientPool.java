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

import com.jiang.mall.domain.config.FtpPoolConfig;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class FtpClientPool {

    private final GenericObjectPool<FTPClient> pool;

    public FtpClientPool(FtpClientFactory factory, FtpPoolConfig config) {
        this.pool = new GenericObjectPool<>(new BasePooledObjectFactory<>() {
            @Override
            public FTPClient create() throws Exception {
                return factory.create();
            }

            @Override
            public PooledObject<FTPClient> wrap(FTPClient ftp) {
                return new DefaultPooledObject<>(ftp);
            }

            @Override
            public boolean validateObject(PooledObject<FTPClient> p) {
                return factory.validate(p.getObject());
            }

            @Override
            public void destroyObject(PooledObject<FTPClient> p) {
                factory.destroy(p.getObject());
            }
        });

        pool.setMaxTotal(config.getMaxTotal());
        pool.setMaxIdle(config.getMaxIdle());
        pool.setMinIdle(config.getMinIdle());
        pool.setMaxWait(Duration.ofMillis(config.getMaxWaitMillis()));
    }

    public FTPClient borrowObject() throws Exception {
        return pool.borrowObject();
    }

    public void returnObject(FTPClient ftp) {
        pool.returnObject(ftp);
    }
}