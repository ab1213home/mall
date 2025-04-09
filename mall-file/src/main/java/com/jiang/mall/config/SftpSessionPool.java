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

import com.jcraft.jsch.Session;
import com.jiang.mall.domain.config.SftpPoolConfig;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class SftpSessionPool {
    private final GenericObjectPool<Session> pool;

    public SftpSessionPool(SftpSessionFactory factory, SftpPoolConfig config) {
        this.pool = new GenericObjectPool<>(new BasePooledObjectFactory<>() {
            @Override
            public Session create() throws Exception {
                return factory.create();
            }

            @Override
            public PooledObject<Session> wrap(Session session) {
                return new DefaultPooledObject<>(session);
            }

            @Override
            public boolean validateObject(PooledObject<Session> p) {
                return factory.validate(p.getObject());
            }

            @Override
            public void destroyObject(PooledObject<Session> p) {
                factory.destroy(p.getObject());
            }
        });

        pool.setMaxTotal(config.getMaxTotal());
        pool.setMaxIdle(config.getMaxIdle());
        pool.setMinIdle(config.getMinIdle());
        pool.setMaxWait(Duration.ofMillis(config.getMaxWaitMillis()));
    }

    public Session borrowObject() throws Exception {
        return pool.borrowObject();
    }

    public void returnObject(Session session) {
        pool.returnObject(session);
    }
}