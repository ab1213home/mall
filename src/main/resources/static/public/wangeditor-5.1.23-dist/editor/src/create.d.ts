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
 * @description create
 * @author wangfupeng
 */
import { Descendant } from 'slate';
import { DOMElement } from './utils/dom';
import { IEditorConfig, IDomEditor, IToolbarConfig, Toolbar } from '@wangeditor/core';
export interface ICreateEditorOption {
    selector: string | DOMElement;
    config: Partial<IEditorConfig>;
    content?: Descendant[];
    html?: string;
    mode: string;
}
export interface ICreateToolbarOption {
    editor: IDomEditor | null;
    selector: string | DOMElement;
    config?: Partial<IToolbarConfig>;
    mode?: string;
}
/**
 * 创建 editor 实例
 */
export declare function createEditor(option?: Partial<ICreateEditorOption>): IDomEditor;
/**
 * 创建 toolbar 实例
 */
export declare function createToolbar(option: ICreateToolbarOption): Toolbar;
