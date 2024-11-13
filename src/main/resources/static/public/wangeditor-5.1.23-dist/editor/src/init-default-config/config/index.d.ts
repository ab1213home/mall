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

/**
 * @description 获取编辑器默认配置
 * @author wangfupeng
 */
export declare function getDefaultEditorConfig(): {
    hoverbarKeys: {
        text: {
            menuKeys: string[];
        };
        link: {
            menuKeys: string[];
        };
        image: {
            menuKeys: string[];
        };
        pre: {
            menuKeys: string[];
        };
        table: {
            menuKeys: string[];
        };
        divider: {
            menuKeys: string[];
        };
        video: {
            menuKeys: string[];
        };
    };
};
export declare function getSimpleEditorConfig(): {
    hoverbarKeys: {
        link: {
            menuKeys: string[];
        };
        image: {
            menuKeys: string[];
        };
        pre: {
            menuKeys: string[];
        };
        table: {
            menuKeys: string[];
        };
        divider: {
            menuKeys: string[];
        };
        video: {
            menuKeys: string[];
        };
    };
};
export declare function getDefaultToolbarConfig(): {
    toolbarKeys: (string | {
        key: string;
        title: string;
        iconSvg: string;
        menuKeys: string[];
    })[];
};
export declare function getSimpleToolbarConfig(): {
    toolbarKeys: (string | {
        key: string;
        title: string;
        iconSvg: string;
        menuKeys: string[];
    })[];
};
