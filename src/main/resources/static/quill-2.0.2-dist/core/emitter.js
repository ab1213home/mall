import {EventEmitter} from 'eventemitter3';
import instances from './instances.js';
import logger from './logger.js';

const debug = logger('quill:events');
const EVENTS = ['selectionchange', 'mousedown', 'mouseup', 'click'];
EVENTS.forEach((eventName) => {
    document.addEventListener(eventName, (...args) => {
        Array.from(document.querySelectorAll('.ql-container')).forEach((node) => {
            const quill = instances.get(node);
            if (quill && quill.emitter) {
                quill.emitter.handleDOM(...args);
            }
        });
    });
});
class Emitter extends EventEmitter {
    constructor() {
        super();
        this.domListeners = {};
        this.on('error', debug.error);
    }
    emit(...args) {
        debug.log.call(debug, ...args);
        // @ts-expect-error
        return super.emit(...args);
    }
    handleDOM(event, ...args) {
        (this.domListeners[event.type] || []).forEach(({ node, handler }) => {
            if (event.target === node || node.contains(event.target)) {
                handler(event, ...args);
            }
        });
    }
    listenDOM(eventName, node, handler) {
        if (!this.domListeners[eventName]) {
            this.domListeners[eventName] = [];
        }
        this.domListeners[eventName].push({ node, handler });
    }
}
Emitter.events = {
    EDITOR_CHANGE: 'editor-change',
    SCROLL_BEFORE_UPDATE: 'scroll-before-update',
    SCROLL_BLOT_MOUNT: 'scroll-blot-mount',
    SCROLL_BLOT_UNMOUNT: 'scroll-blot-unmount',
    SCROLL_OPTIMIZE: 'scroll-optimize',
    SCROLL_UPDATE: 'scroll-update',
    SCROLL_EMBED_UPDATE: 'scroll-embed-update',
    SELECTION_CHANGE: 'selection-change',
    TEXT_CHANGE: 'text-change',
    COMPOSITION_BEFORE_START: 'composition-before-start',
    COMPOSITION_START: 'composition-start',
    COMPOSITION_BEFORE_END: 'composition-before-end',
    COMPOSITION_END: 'composition-end',
};
Emitter.sources = {
    API: 'api',
    SILENT: 'silent',
    USER: 'user',
};
export default Emitter;
//# sourceMappingURL=emitter.js.map