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
 * @description Editor View class
 * @author wangfupeng
 */
import { IDomEditor, IEditorConfig, IToolbarConfig, IModuleConf, IRegisterMenuConf, IRenderElemConf, RenderStyleFnType, IElemToHtmlConf, styleToHtmlFnType, IPreParseHtmlConf, ParseStyleHtmlFnType, IParseElemHtmlConf } from '@wangeditor/core';
declare type PluginType = <T extends IDomEditor>(editor: T) => T;
declare class Boot {
    constructor();
    static editorConfig: Partial<IEditorConfig>;
    static setEditorConfig(newConfig?: Partial<IEditorConfig>): void;
    static simpleEditorConfig: Partial<IEditorConfig>;
    static setSimpleEditorConfig(newConfig?: Partial<IEditorConfig>): void;
    static toolbarConfig: Partial<IToolbarConfig>;
    static setToolbarConfig(newConfig?: Partial<IToolbarConfig>): void;
    static simpleToolbarConfig: Partial<IToolbarConfig>;
    static setSimpleToolbarConfig(newConfig?: Partial<IToolbarConfig>): void;
    static plugins: PluginType[];
    static registerPlugin(plugin: PluginType): void;
    static registerMenu(menuConf: IRegisterMenuConf, customConfig?: {
        [key: string]: any;
    }): void;
    static registerRenderElem(renderElemConf: IRenderElemConf): void;
    static registerRenderStyle(fn: RenderStyleFnType): void;
    static registerElemToHtml(elemToHtmlConf: IElemToHtmlConf): void;
    static registerStyleToHtml(fn: styleToHtmlFnType): void;
    static registerPreParseHtml(preParseHtmlConf: IPreParseHtmlConf): void;
    static registerParseElemHtml(parseElemHtmlConf: IParseElemHtmlConf): void;
    static registerParseStyleHtml(fn: ParseStyleHtmlFnType): void;
    static registerModule(module: Partial<IModuleConf>): void;
}
export default Boot;
