/*!
 * OverlayScrollbars
 * Version: 2.10.0
 *
 * Copyright (c) Rene Haas | KingSora.
 * https://github.com/KingSora
 *
 * Released under the MIT license.
 */
var OverlayScrollbarsGlobal = function(r) {
  "use strict";
  var a = function createCache(r, a) {
    var e = r.v, t = r.i, n = r.o;
    var v = e;
    var i;
    var o = function cacheUpdateContextual(r, a) {
      var e = v;
      var o = r;
      var u = a || (t ? !t(e, o) : e !== o);
      if (u || n) {
        v = o;
        i = e;
      }
      return [ v, u, i ];
    };
    var u = function cacheUpdateIsolated(r) {
      return o(a(v, i), r);
    };
    var c = function getCurrentCache(r) {
      return [ v, !!r, i ];
    };
    return [ a ? u : o, c ];
  };
  var e = typeof window !== "undefined" && typeof HTMLElement !== "undefined" && !!window.document;
  var t = e ? window : {};
  var n = Math.max;
  var v = Math.min;
  var i = Math.round;
  var o = Math.abs;
  var u = Math.sign;
  var c = t.cancelAnimationFrame;
  var l = t.requestAnimationFrame;
  var f = t.setTimeout;
  var s = t.clearTimeout;
  var d = function getApi(r) {
    return typeof t[r] !== "undefined" ? t[r] : void 0;
  };
  var p = d("MutationObserver");
  var _ = d("IntersectionObserver");
  var g = d("ResizeObserver");
  var h = d("ScrollTimeline");
  var b = function isUndefined(r) {
    return r === void 0;
  };
  var m = function isNull(r) {
    return r === null;
  };
  var S = function type(r) {
    return b(r) || m(r) ? "" + r : Object.prototype.toString.call(r).replace(/^\[object (.+)\]$/, "$1").toLowerCase();
  };
  var y = function isNumber(r) {
    return typeof r === "number";
  };
  var w = function isString(r) {
    return typeof r === "string";
  };
  var O = function isBoolean(r) {
    return typeof r === "boolean";
  };
  var C = function isFunction(r) {
    return typeof r === "function";
  };
  var x = function isArray(r) {
    return Array.isArray(r);
  };
  var E = function isObject(r) {
    return typeof r === "object" && !x(r) && !m(r);
  };
  var A = function isArrayLike(r) {
    var a = !!r && r.length;
    var e = y(a) && a > -1 && a % 1 == 0;
    return x(r) || !C(r) && e ? a > 0 && E(r) ? a - 1 in r : true : false;
  };
  var H = function isPlainObject(r) {
    return !!r && r.constructor === Object;
  };
  var P = function isHTMLElement(r) {
    return r instanceof HTMLElement;
  };
  var T = function isElement(r) {
    return r instanceof Element;
  };
  var D = function animationCurrentTime() {
    return performance.now();
  };
  var z = function animateNumber(r, a, e, t, v) {
    var i = 0;
    var o = D();
    var u = n(0, e);
    var f = function frame(e) {
      var c = D();
      var s = c - o;
      var d = s >= u;
      var p = e ? 1 : 1 - (n(0, o + u - c) / u || 0);
      var _ = (a - r) * (C(v) ? v(p, p * u, 0, 1, u) : p) + r;
      var g = d || p === 1;
      t && t(_, p, g);
      i = g ? 0 : l((function() {
        return f();
      }));
    };
    f();
    return function(r) {
      c(i);
      r && f(r);
    };
  };
  function each(r, a) {
    if (A(r)) {
      for (var e = 0; e < r.length; e++) {
        if (a(r[e], e, r) === false) {
          break;
        }
      }
    } else if (r) {
      each(Object.keys(r), (function(e) {
        return a(r[e], e, r);
      }));
    }
    return r;
  }
  var M = function inArray(r, a) {
    return r.indexOf(a) >= 0;
  };
  var I = function concat(r, a) {
    return r.concat(a);
  };
  var L = function push(r, a, e) {
    !w(a) && A(a) ? Array.prototype.push.apply(r, a) : r.push(a);
    return r;
  };
  var V = function from(r) {
    return Array.from(r || []);
  };
  var k = function createOrKeepArray(r) {
    if (x(r)) {
      return r;
    }
    return !w(r) && A(r) ? V(r) : [ r ];
  };
  var R = function isEmptyArray(r) {
    return !!r && !r.length;
  };
  var F = function deduplicateArray(r) {
    return V(new Set(r));
  };
  var N = function runEachAndClear(r, a, e) {
    var t = function runFn(r) {
      return r ? r.apply(void 0, a || []) : true;
    };
    each(r, t);
    !e && (r.length = 0);
  };
  var j = "paddingTop";
  var U = "paddingRight";
  var q = "paddingLeft";
  var B = "paddingBottom";
  var Y = "marginLeft";
  var W = "marginRight";
  var X = "marginBottom";
  var Z = "overflowX";
  var G = "overflowY";
  var $ = "width";
  var J = "height";
  var K = "visible";
  var Q = "hidden";
  var rr = "scroll";
  var ar = function capitalizeFirstLetter(r) {
    var a = String(r || "");
    return a ? a[0].toUpperCase() + a.slice(1) : "";
  };
  var er = function equal(r, a, e, t) {
    if (r && a) {
      var n = true;
      each(e, (function(e) {
        var t = r[e];
        var v = a[e];
        if (t !== v) {
          n = false;
        }
      }));
      return n;
    }
    return false;
  };
  var tr = function equalWH(r, a) {
    return er(r, a, [ "w", "h" ]);
  };
  var nr = function equalXY(r, a) {
    return er(r, a, [ "x", "y" ]);
  };
  var vr = function equalTRBL(r, a) {
    return er(r, a, [ "t", "r", "b", "l" ]);
  };
  var ir = function noop() {};
  var or = function bind(r) {
    for (var a = arguments.length, e = new Array(a > 1 ? a - 1 : 0), t = 1; t < a; t++) {
      e[t - 1] = arguments[t];
    }
    return r.bind.apply(r, [ 0 ].concat(e));
  };
  var ur = function selfClearTimeout(r) {
    var a;
    var e = r ? f : l;
    var t = r ? s : c;
    return [ function(n) {
      t(a);
      a = e((function() {
        return n();
      }), C(r) ? r() : r);
    }, function() {
      return t(a);
    } ];
  };
  var cr = function debounce(r, a) {
    var e = a || {}, t = e.u, n = e.p, v = e._, i = e.m;
    var o;
    var u;
    var d;
    var p;
    var _ = ir;
    var g = function invokeFunctionToDebounce(a) {
      _();
      s(o);
      p = o = u = void 0;
      _ = ir;
      r.apply(this, a);
    };
    var h = function mergeParms(r) {
      return i && u ? i(u, r) : r;
    };
    var b = function flush() {
      if (_ !== ir) {
        g(h(d) || d);
      }
    };
    var m = function debouncedFn() {
      var r = V(arguments);
      var a = C(t) ? t() : t;
      var e = y(a) && a >= 0;
      if (e) {
        var i = C(n) ? n() : n;
        var m = y(i) && i >= 0;
        var S = a > 0 ? f : l;
        var w = a > 0 ? s : c;
        var O = h(r);
        var x = O || r;
        var E = g.bind(0, x);
        var A;
        _();
        if (v && !p) {
          E();
          p = true;
          A = S((function() {
            return p = void 0;
          }), a);
        } else {
          A = S(E, a);
          if (m && !o) {
            o = f(b, i);
          }
        }
        _ = function clear() {
          return w(A);
        };
        u = d = x;
      } else {
        g(r);
      }
    };
    m.S = b;
    return m;
  };
  var lr = function hasOwnProperty(r, a) {
    return Object.prototype.hasOwnProperty.call(r, a);
  };
  var fr = function keys(r) {
    return r ? Object.keys(r) : [];
  };
  var sr = function assignDeep(r, a, e, t, n, v, i) {
    var o = [ a, e, t, n, v, i ];
    if ((typeof r !== "object" || m(r)) && !C(r)) {
      r = {};
    }
    each(o, (function(a) {
      each(a, (function(e, t) {
        var n = a[t];
        if (r === n) {
          return true;
        }
        var v = x(n);
        if (n && H(n)) {
          var i = r[t];
          var o = i;
          if (v && !x(i)) {
            o = [];
          } else if (!v && !H(i)) {
            o = {};
          }
          r[t] = sr(o, n);
        } else {
          r[t] = v ? n.slice() : n;
        }
      }));
    }));
    return r;
  };
  var dr = function removeUndefinedProperties(r, a) {
    return each(sr({}, r), (function(r, a, e) {
      if (r === void 0) {
        delete e[a];
      } else if (r && H(r)) {
        e[a] = dr(r);
      }
    }));
  };
  var pr = function isEmptyObject(r) {
    return !fr(r).length;
  };
  var _r = function capNumber(r, a, e) {
    return n(r, v(a, e));
  };
  var gr = function getDomTokensArray(r) {
    return F((x(r) ? r : (r || "").split(" ")).filter((function(r) {
      return r;
    })));
  };
  var hr = function getAttr(r, a) {
    return r && r.getAttribute(a);
  };
  var br = function hasAttr(r, a) {
    return r && r.hasAttribute(a);
  };
  var mr = function setAttrs(r, a, e) {
    each(gr(a), (function(a) {
      r && r.setAttribute(a, String(e || ""));
    }));
  };
  var Sr = function removeAttrs(r, a) {
    each(gr(a), (function(a) {
      return r && r.removeAttribute(a);
    }));
  };
  var yr = function domTokenListAttr(r, a) {
    var e = gr(hr(r, a));
    var t = or(mr, r, a);
    var n = function domTokenListOperation(r, a) {
      var t = new Set(e);
      each(gr(r), (function(r) {
        t[a](r);
      }));
      return V(t).join(" ");
    };
    return {
      O: function _remove(r) {
        return t(n(r, "delete"));
      },
      C: function _add(r) {
        return t(n(r, "add"));
      },
      A: function _has(r) {
        var a = gr(r);
        return a.reduce((function(r, a) {
          return r && e.includes(a);
        }), a.length > 0);
      }
    };
  };
  var wr = function removeAttrClass(r, a, e) {
    yr(r, a).O(e);
    return or(Or, r, a, e);
  };
  var Or = function addAttrClass(r, a, e) {
    yr(r, a).C(e);
    return or(wr, r, a, e);
  };
  var Cr = function addRemoveAttrClass(r, a, e, t) {
    return (t ? Or : wr)(r, a, e);
  };
  var xr = function hasAttrClass(r, a, e) {
    return yr(r, a).A(e);
  };
  var Er = function createDomTokenListClass(r) {
    return yr(r, "class");
  };
  var Ar = function removeClass(r, a) {
    Er(r).O(a);
  };
  var Hr = function addClass(r, a) {
    Er(r).C(a);
    return or(Ar, r, a);
  };
  var Pr = function find(r, a) {
    var e = a ? T(a) && a : document;
    return e ? V(e.querySelectorAll(r)) : [];
  };
  var Tr = function findFirst(r, a) {
    var e = a ? T(a) && a : document;
    return e && e.querySelector(r);
  };
  var Dr = function is(r, a) {
    return T(r) && r.matches(a);
  };
  var zr = function isBodyElement(r) {
    return Dr(r, "body");
  };
  var Mr = function contents(r) {
    return r ? V(r.childNodes) : [];
  };
  var Ir = function parent(r) {
    return r && r.parentElement;
  };
  var Lr = function closest(r, a) {
    return T(r) && r.closest(a);
  };
  var Vr = function getFocusedElement(r) {
    return document.activeElement;
  };
  var kr = function liesBetween(r, a, e) {
    var t = Lr(r, a);
    var n = r && Tr(e, t);
    var v = Lr(n, a) === t;
    return t && n ? t === r || n === r || v && Lr(Lr(r, e), a) !== t : false;
  };
  var Rr = function removeElements(r) {
    each(k(r), (function(r) {
      var a = Ir(r);
      r && a && a.removeChild(r);
    }));
  };
  var Fr = function appendChildren(r, a) {
    return or(Rr, r && a && each(k(a), (function(a) {
      a && r.appendChild(a);
    })));
  };
  var Nr = function createDiv(r) {
    var a = document.createElement("div");
    mr(a, "class", r);
    return a;
  };
  var jr = function createDOM(r) {
    var a = Nr();
    a.innerHTML = r.trim();
    return each(Mr(a), (function(r) {
      return Rr(r);
    }));
  };
  var Ur = function getCSSVal(r, a) {
    return r.getPropertyValue(a) || r[a] || "";
  };
  var qr = function validFiniteNumber(r) {
    var a = r || 0;
    return isFinite(a) ? a : 0;
  };
  var Br = function parseToZeroOrNumber(r) {
    return qr(parseFloat(r || ""));
  };
  var Yr = function roundCssNumber(r) {
    return Math.round(r * 1e4) / 1e4;
  };
  var Wr = function numberToCssPx(r) {
    return Yr(qr(r)) + "px";
  };
  function setStyles(r, a) {
    r && a && each(a, (function(a, e) {
      try {
        var t = r.style;
        var n = m(a) || O(a) ? "" : y(a) ? Wr(a) : a;
        if (e.indexOf("--") === 0) {
          t.setProperty(e, n);
        } else {
          t[e] = n;
        }
      } catch (v) {}
    }));
  }
  function getStyles(r, a, e) {
    var n = w(a);
    var v = n ? "" : {};
    if (r) {
      var i = t.getComputedStyle(r, e) || r.style;
      v = n ? Ur(i, a) : V(a).reduce((function(r, a) {
        r[a] = Ur(i, a);
        return r;
      }), v);
    }
    return v;
  }
  var Xr = function topRightBottomLeft(r, a, e) {
    var t = a ? a + "-" : "";
    var n = e ? "-" + e : "";
    var v = t + "top" + n;
    var i = t + "right" + n;
    var o = t + "bottom" + n;
    var u = t + "left" + n;
    var c = getStyles(r, [ v, i, o, u ]);
    return {
      t: Br(c[v]),
      r: Br(c[i]),
      b: Br(c[o]),
      l: Br(c[u])
    };
  };
  var Zr = function getTrasformTranslateValue(r, a) {
    return "translate" + (E(r) ? "(" + r.x + "," + r.y + ")" : "Y" + "(" + r + ")");
  };
  var Gr = function elementHasDimensions(r) {
    return !!(r.offsetWidth || r.offsetHeight || r.getClientRects().length);
  };
  var $r = {
    w: 0,
    h: 0
  };
  var Jr = function getElmWidthHeightProperty(r, a) {
    return a ? {
      w: a[r + "Width"],
      h: a[r + "Height"]
    } : $r;
  };
  var Kr = function getWindowSize(r) {
    return Jr("inner", r || t);
  };
  var Qr = or(Jr, "offset");
  var ra = or(Jr, "client");
  var aa = or(Jr, "scroll");
  var ea = function getFractionalSize(r) {
    var a = parseFloat(getStyles(r, $)) || 0;
    var e = parseFloat(getStyles(r, J)) || 0;
    return {
      w: a - i(a),
      h: e - i(e)
    };
  };
  var ta = function getBoundingClientRect(r) {
    return r.getBoundingClientRect();
  };
  var na = function hasDimensions(r) {
    return !!r && Gr(r);
  };
  var va = function domRectHasDimensions(r) {
    return !!(r && (r[J] || r[$]));
  };
  var ia = function domRectAppeared(r, a) {
    var e = va(r);
    var t = va(a);
    return !t && e;
  };
  var oa = function removeEventListener(r, a, e, t) {
    each(gr(a), (function(a) {
      r && r.removeEventListener(a, e, t);
    }));
  };
  var ua = function addEventListener(r, a, e, t) {
    var n;
    var v = (n = t && t.H) != null ? n : true;
    var i = t && t.P || false;
    var o = t && t.T || false;
    var u = {
      passive: v,
      capture: i
    };
    return or(N, gr(a).map((function(a) {
      var t = o ? function(n) {
        oa(r, a, t, i);
        e && e(n);
      } : e;
      r && r.addEventListener(a, t, u);
      return or(oa, r, a, t, i);
    })));
  };
  var ca = function stopPropagation(r) {
    return r.stopPropagation();
  };
  var la = function preventDefault(r) {
    return r.preventDefault();
  };
  var fa = function stopAndPrevent(r) {
    return ca(r) || la(r);
  };
  var sa = function scrollElementTo(r, a) {
    var e = y(a) ? {
      x: a,
      y: a
    } : a || {}, t = e.x, n = e.y;
    y(t) && (r.scrollLeft = t);
    y(n) && (r.scrollTop = n);
  };
  var da = function getElementScroll(r) {
    return {
      x: r.scrollLeft,
      y: r.scrollTop
    };
  };
  var pa = function getZeroScrollCoordinates() {
    return {
      D: {
        x: 0,
        y: 0
      },
      M: {
        x: 0,
        y: 0
      }
    };
  };
  var _a = function sanitizeScrollCoordinates(r, a) {
    var e = r.D, t = r.M;
    var n = a.w, v = a.h;
    var i = function sanitizeAxis(r, a, e) {
      var t = u(r) * e;
      var n = u(a) * e;
      if (t === n) {
        var v = o(r);
        var i = o(a);
        n = v > i ? 0 : n;
        t = v < i ? 0 : t;
      }
      t = t === n ? 0 : t;
      return [ t + 0, n + 0 ];
    };
    var c = i(e.x, t.x, n), l = c[0], f = c[1];
    var s = i(e.y, t.y, v), d = s[0], p = s[1];
    return {
      D: {
        x: l,
        y: d
      },
      M: {
        x: f,
        y: p
      }
    };
  };
  var ga = function isDefaultDirectionScrollCoordinates(r) {
    var a = r.D, e = r.M;
    var t = function getAxis(r, a) {
      return r === 0 && r <= a;
    };
    return {
      x: t(a.x, e.x),
      y: t(a.y, e.y)
    };
  };
  var ha = function getScrollCoordinatesPercent(r, a) {
    var e = r.D, t = r.M;
    var n = function getAxis(r, a, e) {
      return _r(0, 1, (r - e) / (r - a) || 0);
    };
    return {
      x: n(e.x, t.x, a.x),
      y: n(e.y, t.y, a.y)
    };
  };
  var ba = function focusElement(r) {
    if (r && r.focus) {
      r.focus({
        preventScroll: true
      });
    }
  };
  var ma = function manageListener(r, a) {
    each(k(a), r);
  };
  var Sa = function createEventListenerHub(r) {
    var a = new Map;
    var e = function removeEvent(r, e) {
      if (r) {
        var t = a.get(r);
        ma((function(r) {
          if (t) {
            t[r ? "delete" : "clear"](r);
          }
        }), e);
      } else {
        a.forEach((function(r) {
          r.clear();
        }));
        a.clear();
      }
    };
    var t = function addEvent(r, n) {
      if (w(r)) {
        var v = a.get(r) || new Set;
        a.set(r, v);
        ma((function(r) {
          C(r) && v.add(r);
        }), n);
        return or(e, r, n);
      }
      if (O(n) && n) {
        e();
      }
      var i = fr(r);
      var o = [];
      each(i, (function(a) {
        var e = r[a];
        e && L(o, t(a, e));
      }));
      return or(N, o);
    };
    var n = function triggerEvent(r, e) {
      each(V(a.get(r)), (function(r) {
        if (e && !R(e)) {
          r.apply(0, e);
        } else {
          r();
        }
      }));
    };
    t(r || {});
    return [ t, e, n ];
  };
  var ya = function opsStringify(r) {
    return JSON.stringify(r, (function(r, a) {
      if (C(a)) {
        throw 0;
      }
      return a;
    }));
  };
  var wa = function getPropByPath(r, a) {
    return r ? ("" + a).split(".").reduce((function(r, a) {
      return r && lr(r, a) ? r[a] : void 0;
    }), r) : void 0;
  };
  var Oa = {
    paddingAbsolute: false,
    showNativeOverlaidScrollbars: false,
    update: {
      elementEvents: [ [ "img", "load" ] ],
      debounce: [ 0, 33 ],
      attributes: null,
      ignoreMutation: null
    },
    overflow: {
      x: "scroll",
      y: "scroll"
    },
    scrollbars: {
      theme: "os-theme-dark",
      visibility: "auto",
      autoHide: "never",
      autoHideDelay: 1300,
      autoHideSuspend: false,
      dragScroll: true,
      clickScroll: false,
      pointers: [ "mouse", "touch", "pen" ]
    }
  };
  var Ca = function getOptionsDiff(r, a) {
    var e = {};
    var t = I(fr(a), fr(r));
    each(t, (function(t) {
      var n = r[t];
      var v = a[t];
      if (E(n) && E(v)) {
        sr(e[t] = {}, Ca(n, v));
        if (pr(e[t])) {
          delete e[t];
        }
      } else if (lr(a, t) && v !== n) {
        var i = true;
        if (x(n) || x(v)) {
          try {
            if (ya(n) === ya(v)) {
              i = false;
            }
          } catch (o) {}
        }
        if (i) {
          e[t] = v;
        }
      }
    }));
    return e;
  };
  var xa = function createOptionCheck(r, a, e) {
    return function(t) {
      return [ wa(r, t), e || wa(a, t) !== void 0 ];
    };
  };
  var Ea = "data-overlayscrollbars";
  var Aa = "os-environment";
  var Ha = Aa + "-scrollbar-hidden";
  var Pa = Ea + "-initialize";
  var Ta = "noClipping";
  var Da = Ea + "-body";
  var za = Ea;
  var Ma = "host";
  var Ia = Ea + "-viewport";
  var La = Z;
  var Va = G;
  var ka = "arrange";
  var Ra = "measuring";
  var Fa = "scrolling";
  var Na = "scrollbarHidden";
  var ja = "noContent";
  var Ua = Ea + "-padding";
  var qa = Ea + "-content";
  var Ba = "os-size-observer";
  var Ya = Ba + "-appear";
  var Wa = Ba + "-listener";
  var Xa = Wa + "-scroll";
  var Za = Wa + "-item";
  var Ga = Za + "-final";
  var $a = "os-trinsic-observer";
  var Ja = "os-theme-none";
  var Ka = "os-scrollbar";
  var Qa = Ka + "-rtl";
  var re = Ka + "-horizontal";
  var ae = Ka + "-vertical";
  var ee = Ka + "-track";
  var te = Ka + "-handle";
  var ne = Ka + "-visible";
  var ve = Ka + "-cornerless";
  var ie = Ka + "-interaction";
  var oe = Ka + "-unusable";
  var ue = Ka + "-auto-hide";
  var ce = ue + "-hidden";
  var le = Ka + "-wheel";
  var fe = ee + "-interactive";
  var se = te + "-interactive";
  var de;
  var pe = function getNonce() {
    return de;
  };
  var _e = function setNonce(r) {
    de = r;
  };
  var ge;
  var he = function createEnvironment() {
    var r = function getNativeScrollbarSize(r, a, e) {
      Fr(document.body, r);
      Fr(document.body, r);
      var t = ra(r);
      var n = Qr(r);
      var v = ea(a);
      e && Rr(r);
      return {
        x: n.h - t.h + v.h,
        y: n.w - t.w + v.w
      };
    };
    var e = function getNativeScrollbarsHiding(r) {
      var a = false;
      var e = Hr(r, Ha);
      try {
        a = getStyles(r, "scrollbar-width") === "none" || getStyles(r, "display", "::-webkit-scrollbar") === "none";
      } catch (t) {}
      e();
      return a;
    };
    var n = "." + Aa + "{scroll-behavior:auto!important;position:fixed;opacity:0;visibility:hidden;overflow:scroll;height:200px;width:200px;z-index:-1}." + Aa + " div{width:200%;height:200%;margin:10px 0}." + Ha + "{scrollbar-width:none!important}." + Ha + "::-webkit-scrollbar,." + Ha + "::-webkit-scrollbar-corner{appearance:none!important;display:none!important;width:0!important;height:0!important}";
    var v = jr('<div class="' + Aa + '"><div></div><style>' + n + "</style></div>");
    var i = v[0];
    var o = i.firstChild;
    var u = i.lastChild;
    var c = pe();
    if (c) {
      u.nonce = c;
    }
    var l = Sa(), f = l[0], s = l[2];
    var d = a({
      v: r(i, o),
      i: nr
    }, or(r, i, o, true)), p = d[0], _ = d[1];
    var g = _(), b = g[0];
    var m = e(i);
    var S = {
      x: b.x === 0,
      y: b.y === 0
    };
    var y = {
      elements: {
        host: null,
        padding: !m,
        viewport: function viewport(r) {
          return m && zr(r) && r;
        },
        content: false
      },
      scrollbars: {
        slot: true
      },
      cancel: {
        nativeScrollbarsOverlaid: false,
        body: null
      }
    };
    var w = sr({}, Oa);
    var O = or(sr, {}, w);
    var x = or(sr, {}, y);
    var E = {
      I: b,
      L: S,
      V: m,
      k: !!h,
      R: or(f, "r"),
      F: x,
      N: function _setDefaultInitialization(r) {
        return sr(y, r) && x();
      },
      j: O,
      U: function _setDefaultOptions(r) {
        return sr(w, r) && O();
      },
      q: sr({}, y),
      B: sr({}, w)
    };
    Sr(i, "style");
    Rr(i);
    ua(t, "resize", (function() {
      s("r", []);
    }));
    if (C(t.matchMedia) && !m && (!S.x || !S.y)) {
      var A = function addZoomListener(r) {
        var a = t.matchMedia("(resolution: " + t.devicePixelRatio + "dppx)");
        ua(a, "change", (function() {
          r();
          A(r);
        }), {
          T: true
        });
      };
      A((function() {
        var r = p(), a = r[0], e = r[1];
        sr(E.I, a);
        s("r", [ e ]);
      }));
    }
    return E;
  };
  var be = function getEnvironment() {
    if (!ge) {
      ge = he();
    }
    return ge;
  };
  var me = function resolveInitialization(r, a) {
    return C(a) ? a.apply(0, r) : a;
  };
  var Se = function staticInitializationElement(r, a, e, t) {
    var n = b(t) ? e : t;
    var v = me(r, n);
    return v || a.apply(0, r);
  };
  var ye = function dynamicInitializationElement(r, a, e, t) {
    var n = b(t) ? e : t;
    var v = me(r, n);
    return !!v && (P(v) ? v : a.apply(0, r));
  };
  var we = function cancelInitialization(r, a) {
    var e = a || {}, t = e.nativeScrollbarsOverlaid, n = e.body;
    var v = be(), i = v.L, o = v.V, u = v.F;
    var c = u().cancel, l = c.nativeScrollbarsOverlaid, f = c.body;
    var s = t != null ? t : l;
    var d = b(n) ? f : n;
    var p = (i.x || i.y) && s;
    var _ = r && (m(d) ? !o : d);
    return !!p || !!_;
  };
  var Oe = new WeakMap;
  var Ce = function addInstance(r, a) {
    Oe.set(r, a);
  };
  var xe = function removeInstance(r) {
    Oe.delete(r);
  };
  var Ee = function getInstance(r) {
    return Oe.get(r);
  };
  var Ae = function createEventContentChange(r, a, e) {
    var t = false;
    var n = e ? new WeakMap : false;
    var v = function destroy() {
      t = true;
    };
    var i = function updateElements(v) {
      if (n && e) {
        var i = e.map((function(a) {
          var e = a || [], t = e[0], n = e[1];
          var i = n && t ? (v || Pr)(t, r) : [];
          return [ i, n ];
        }));
        each(i, (function(e) {
          return each(e[0], (function(v) {
            var i = e[1];
            var o = n.get(v) || [];
            var u = r.contains(v);
            if (u && i) {
              var c = ua(v, i, (function(r) {
                if (t) {
                  c();
                  n.delete(v);
                } else {
                  a(r);
                }
              }));
              n.set(v, L(o, c));
            } else {
              N(o);
              n.delete(v);
            }
          }));
        }));
      }
    };
    i();
    return [ v, i ];
  };
  var He = function createDOMObserver(r, a, e, t) {
    var n = false;
    var v = t || {}, i = v.Y, o = v.W, u = v.X, c = v.Z, l = v.G, f = v.$;
    var s = cr((function() {
      return n && e(true);
    }), {
      u: 33,
      p: 99
    });
    var d = Ae(r, s, u), _ = d[0], g = d[1];
    var h = i || [];
    var b = o || [];
    var m = I(h, b);
    var S = function observerCallback(n, v) {
      if (!R(v)) {
        var i = l || ir;
        var o = f || ir;
        var u = [];
        var s = [];
        var d = false;
        var p = false;
        each(v, (function(e) {
          var n = e.attributeName, v = e.target, l = e.type, f = e.oldValue, _ = e.addedNodes, g = e.removedNodes;
          var h = l === "attributes";
          var m = l === "childList";
          var S = r === v;
          var y = h && n;
          var O = y && hr(v, n || "");
          var C = w(O) ? O : null;
          var x = y && f !== C;
          var E = M(b, n) && x;
          if (a && (m || !S)) {
            var A = h && x;
            var H = A && c && Dr(v, c);
            var P = H ? !i(v, n, f, C) : !h || A;
            var T = P && !o(e, !!H, r, t);
            each(_, (function(r) {
              return L(u, r);
            }));
            each(g, (function(r) {
              return L(u, r);
            }));
            p = p || T;
          }
          if (!a && S && x && !i(v, n, f, C)) {
            L(s, n);
            d = d || E;
          }
        }));
        g((function(r) {
          return F(u).reduce((function(a, e) {
            L(a, Pr(r, e));
            return Dr(e, r) ? L(a, e) : a;
          }), []);
        }));
        if (a) {
          !n && p && e(false);
          return [ false ];
        }
        if (!R(s) || d) {
          var _ = [ F(s), d ];
          !n && e.apply(0, _);
          return _;
        }
      }
    };
    var y = new p(or(S, false));
    return [ function() {
      y.observe(r, {
        attributes: true,
        attributeOldValue: true,
        attributeFilter: m,
        subtree: a,
        childList: a,
        characterData: a
      });
      n = true;
      return function() {
        if (n) {
          _();
          y.disconnect();
          n = false;
        }
      };
    }, function() {
      if (n) {
        s.S();
        return S(true, y.takeRecords());
      }
    } ];
  };
  var Pe = {};
  var Te = {};
  var De = function addPlugins(r) {
    each(r, (function(r) {
      return each(r, (function(a, e) {
        Pe[e] = r[e];
      }));
    }));
  };
  var ze = function registerPluginModuleInstances(r, a, e) {
    return fr(r).map((function(t) {
      var n = r[t], v = n.static, i = n.instance;
      var o = e || [], u = o[0], c = o[1], l = o[2];
      var f = e ? i : v;
      if (f) {
        var s = e ? f(u, c, a) : f(a);
        return (l || Te)[t] = s;
      }
    }));
  };
  var Me = function getStaticPluginModuleInstance(r) {
    return Te[r];
  };
  function getDefaultExportFromCjs(r) {
    return r && r.J && Object.prototype.hasOwnProperty.call(r, "default") ? r["default"] : r;
  }
  var Ie = {
    exports: {}
  };
  (function(r) {
    function _extends() {
      return r.exports = _extends = Object.assign ? Object.assign.bind() : function(r) {
        for (var a = 1; a < arguments.length; a++) {
          var e = arguments[a];
          for (var t in e) {
            ({}).hasOwnProperty.call(e, t) && (r[t] = e[t]);
          }
        }
        return r;
      }, r.exports.J = true, r.exports["default"] = r.exports, _extends.apply(null, arguments);
    }
    r.exports = _extends, r.exports.J = true, r.exports["default"] = r.exports;
  })(Ie);
  var Le = Ie.exports;
  var Ve = /*@__PURE__*/ getDefaultExportFromCjs(Le);
  var ke = {
    boolean: "__TPL_boolean_TYPE__",
    number: "__TPL_number_TYPE__",
    string: "__TPL_string_TYPE__",
    array: "__TPL_array_TYPE__",
    object: "__TPL_object_TYPE__",
    function: "__TPL_function_TYPE__",
    null: "__TPL_null_TYPE__"
  };
  var Re = function validateRecursive(r, a, e, t) {
    var n = {};
    var v = Ve({}, a);
    var i = fr(r).filter((function(r) {
      return lr(a, r);
    }));
    each(i, (function(i) {
      var o = a[i];
      var u = r[i];
      var c = H(u);
      var l = t ? t + "." : "";
      if (c && H(o)) {
        var f = Re(u, o, e, l + i), s = f[0], d = f[1];
        n[i] = s;
        v[i] = d;
        each([ v, n ], (function(r) {
          if (pr(r[i])) {
            delete r[i];
          }
        }));
      } else if (!c) {
        var p = false;
        var _ = [];
        var g = [];
        var h = S(o);
        var m = k(u);
        each(m, (function(r) {
          var a;
          each(ke, (function(e, t) {
            if (e === r) {
              a = t;
            }
          }));
          var e = b(a);
          if (e && w(o)) {
            var t = r.split(" ");
            p = !!t.find((function(r) {
              return r === o;
            }));
            L(_, t);
          } else {
            p = ke[h] === r;
          }
          L(g, e ? ke.string : a);
          return !p;
        }));
        if (p) {
          n[i] = o;
        } else if (e) {
          console.warn('The option "' + l + i + "\" wasn't set, because it doesn't accept the type [ " + h.toUpperCase() + ' ] with the value of "' + o + '".\r\n' + "Accepted types are: [ " + g.join(", ").toUpperCase() + " ].\r\n" + (_.length > 0 ? "\r\nValid strings are: [ " + _.join(", ") + " ]." : ""));
        }
        delete v[i];
      }
    }));
    return [ n, v ];
  };
  var Fe = function validateOptions(r, a, e) {
    return Re(r, a, e);
  };
  var Ne = "__osOptionsValidationPlugin";
  /* @__PURE__ */  (function(r) {
    return r = {}, r[Ne] = {
      static: function _static() {
        var r = ke.number;
        var a = ke.boolean;
        var e = [ ke.array, ke.null ];
        var t = "hidden scroll visible visible-hidden";
        var n = "visible hidden auto";
        var v = "never scroll leavemove";
        var i = [ a, ke.string ];
        var o = {
          paddingAbsolute: a,
          showNativeOverlaidScrollbars: a,
          update: {
            elementEvents: e,
            attributes: e,
            debounce: [ ke.number, ke.array, ke.null ],
            ignoreMutation: [ ke.function, ke.null ]
          },
          overflow: {
            x: t,
            y: t
          },
          scrollbars: {
            theme: [ ke.string, ke.null ],
            visibility: n,
            autoHide: v,
            autoHideDelay: r,
            autoHideSuspend: a,
            dragScroll: a,
            clickScroll: i,
            pointers: [ ke.array, ke.null ]
          }
        };
        return function(r, a) {
          var e = Fe(o, r, a), t = e[0], n = e[1];
          return Ve({}, n, t);
        };
      }
    }, r;
  })();
  var je = "__osSizeObserverPlugin";
  var Ue = /* @__PURE__ */ function(r) {
    return r = {}, r[je] = {
      static: function _static() {
        return function(r, a, e) {
          var t;
          var n = 3333333;
          var v = "scroll";
          var i = jr('<div class="' + Za + '" dir="ltr"><div class="' + Za + '"><div class="' + Ga + '"></div></div><div class="' + Za + '"><div class="' + Ga + '" style="width: 200%; height: 200%"></div></div></div>');
          var o = i[0];
          var u = o.lastChild;
          var f = o.firstChild;
          var s = f == null ? void 0 : f.firstChild;
          var d = Qr(o);
          var p = d;
          var _ = false;
          var g;
          var h = function reset() {
            sa(f, n);
            sa(u, n);
          };
          var b = function onResized(r) {
            g = 0;
            if (_) {
              d = p;
              a(r === true);
            }
          };
          var m = function onScroll(r) {
            p = Qr(o);
            _ = !r || !tr(p, d);
            if (r) {
              ca(r);
              if (_ && !g) {
                c(g);
                g = l(b);
              }
            } else {
              b(r === false);
            }
            h();
          };
          var S = [ Fr(r, i), ua(f, v, m), ua(u, v, m) ];
          Hr(r, Xa);
          setStyles(s, (t = {}, t[$] = n, t[J] = n, t));
          l(h);
          return [ e ? or(m, false) : h, S ];
        };
      }
    }, r;
  }();
  var qe = function getShowNativeOverlaidScrollbars(r, a) {
    var e = a.L;
    var t = r("showNativeOverlaidScrollbars"), n = t[0], v = t[1];
    return [ n && e.x && e.y, v ];
  };
  var Be = function overflowIsVisible(r) {
    return r.indexOf(K) === 0;
  };
  var Ye = function createViewportOverflowState(r, a) {
    var e = function getAxisOverflowStyle(r, a, e, t) {
      var n = r === K ? Q : r.replace(K + "-", "");
      var v = Be(r);
      var i = Be(e);
      if (!a && !t) {
        return Q;
      }
      if (v && i) {
        return K;
      }
      if (v) {
        var o = a ? K : Q;
        return a && t ? n : o;
      }
      var u = i && t ? K : Q;
      return a ? n : u;
    };
    var t = {
      x: e(a.x, r.x, a.y, r.y),
      y: e(a.y, r.y, a.x, r.x)
    };
    return {
      K: t,
      rr: {
        x: t.x === rr,
        y: t.y === rr
      }
    };
  };
  var We = "__osScrollbarsHidingPlugin";
  var Xe = /* @__PURE__ */ function(r) {
    return r = {}, r[We] = {
      static: function _static() {
        return {
          ar: function _viewportArrangement(r, a, e, t, n) {
            var v = r.er, i = r.tr;
            var o = t.V, u = t.L, c = t.I;
            var l = !v && !o && (u.x || u.y);
            var f = qe(n, t), s = f[0];
            var d = function readViewportOverflowState() {
              var r = function getStatePerAxis(r) {
                var a = getStyles(i, r);
                var e = a === rr;
                return [ a, e ];
              };
              var a = r(Z), e = a[0], t = a[1];
              var n = r(G), v = n[0], o = n[1];
              return {
                K: {
                  x: e,
                  y: v
                },
                rr: {
                  x: t,
                  y: o
                }
              };
            };
            var p = function _getViewportOverflowHideOffset(r) {
              var a = r.rr;
              var e = o || s ? 0 : 42;
              var t = function getHideOffsetPerAxis(r, a, t) {
                var n = r ? e : t;
                var v = a && !o ? n : 0;
                var i = r && !!e;
                return [ v, i ];
              };
              var n = t(u.x, a.x, c.x), v = n[0], i = n[1];
              var l = t(u.y, a.y, c.y), f = l[0], d = l[1];
              return {
                nr: {
                  x: v,
                  y: f
                },
                vr: {
                  x: i,
                  y: d
                }
              };
            };
            var _ = function _hideNativeScrollbars(r, e, t) {
              var n = e.ir;
              if (!v) {
                var i;
                var o = sr({}, (i = {}, i[W] = 0, i[X] = 0, i[Y] = 0, i));
                var u = p(r), c = u.nr, l = u.vr;
                var f = l.x, s = l.y;
                var d = c.x, _ = c.y;
                var g = a.ur;
                var h = n ? Y : W;
                var b = n ? q : U;
                var m = g[h];
                var S = g[X];
                var y = g[b];
                var w = g[B];
                o[$] = "calc(100% + " + (_ + m * -1) + "px)";
                o[h] = -_ + m;
                o[X] = -d + S;
                if (t) {
                  o[b] = y + (s ? _ : 0);
                  o[B] = w + (f ? d : 0);
                }
                return o;
              }
            };
            var g = function _arrangeViewport(r, t, n) {
              if (l) {
                var v = a.ur;
                var o = p(r), u = o.nr, c = o.vr;
                var f = c.x, s = c.y;
                var d = u.x, _ = u.y;
                var g = e.ir;
                var h = g ? U : q;
                var b = v[h];
                var m = v.paddingTop;
                var S = t.w + n.w;
                var y = t.h + n.h;
                var w = {
                  w: _ && s ? _ + S - b + "px" : "",
                  h: d && f ? d + y - m + "px" : ""
                };
                setStyles(i, {
                  "--os-vaw": w.w,
                  "--os-vah": w.h
                });
              }
              return l;
            };
            var h = function _undoViewportArrange(r) {
              if (l) {
                var t = r || d();
                var n = a.ur;
                var v = p(t), o = v.vr;
                var u = o.x, c = o.y;
                var f = {};
                var s = function assignProps(r) {
                  return each(r, (function(r) {
                    f[r] = n[r];
                  }));
                };
                if (u) {
                  s([ X, j, B ]);
                }
                if (c) {
                  s([ Y, W, q, U ]);
                }
                var g = getStyles(i, fr(f));
                var h = wr(i, Ia, ka);
                setStyles(i, f);
                return [ function() {
                  setStyles(i, sr({}, g, _(t, e, l)));
                  h();
                }, t ];
              }
              return [ ir ];
            };
            return {
              cr: p,
              lr: g,
              sr: h,
              dr: _
            };
          }
        };
      }
    }, r;
  }();
  var Ze = "__osClickScrollPlugin";
  var Ge = /* @__PURE__ */ function(r) {
    return r = {}, r[Ze] = {
      static: function _static() {
        return function(r, a, e, t) {
          var n = false;
          var v = ir;
          var i = 133;
          var o = 222;
          var u = ur(i), c = u[0], l = u[1];
          var f = Math.sign(a);
          var s = e * f;
          var d = s / 2;
          var p = function easing(r) {
            return 1 - (1 - r) * (1 - r);
          };
          var _ = function easedEndPressAnimation(a, e) {
            return z(a, e, o, r, p);
          };
          var g = function linearPressAnimation(e, t) {
            return z(e, a - s, i * t, (function(e, t, n) {
              r(e);
              if (n) {
                v = _(e, a);
              }
            }));
          };
          var h = z(0, s, o, (function(i, o, u) {
            r(i);
            if (u) {
              t(n);
              if (!n) {
                var l = a - i;
                var p = Math.sign(l - d) === f;
                p && c((function() {
                  var r = l - s;
                  var t = Math.sign(r) === f;
                  v = t ? g(i, Math.abs(r) / e) : _(i, a);
                }));
              }
            }
          }), p);
          return function(r) {
            n = true;
            if (r) {
              h();
            }
            l();
            v();
          };
        };
      }
    }, r;
  }();
  var $e = function createSizeObserver(r, e, t) {
    var n = t || {}, v = n.pr;
    var i = Me(je);
    var o = a({
      v: false,
      o: true
    }), u = o[0];
    return function() {
      var a = [];
      var t = jr('<div class="' + Ba + '"><div class="' + Wa + '"></div></div>');
      var n = t[0];
      var o = n.firstChild;
      var c = function onSizeChangedCallbackProxy(r) {
        var a = r instanceof ResizeObserverEntry;
        var t = false;
        var n = false;
        if (a) {
          var v = u(r.contentRect), i = v[0], o = v[2];
          var c = va(i);
          n = ia(i, o);
          t = !n && !c;
        } else {
          n = r === true;
        }
        if (!t) {
          e({
            _r: true,
            pr: n
          });
        }
      };
      if (g) {
        var l = new g((function(r) {
          return c(r.pop());
        }));
        l.observe(o);
        L(a, (function() {
          l.disconnect();
        }));
      } else if (i) {
        var f = i(o, c, v), s = f[0], d = f[1];
        L(a, I([ Hr(n, Ya), ua(n, "animationstart", s) ], d));
      } else {
        return ir;
      }
      return or(N, L(a, Fr(r, n)));
    };
  };
  var Je = function createTrinsicObserver(r, e) {
    var t;
    var n = function isHeightIntrinsic(r) {
      return r.h === 0 || r.isIntersecting || r.intersectionRatio > 0;
    };
    var v = Nr($a);
    var i = a({
      v: false
    }), o = i[0];
    var u = function triggerOnTrinsicChangedCallback(r, a) {
      if (r) {
        var t = o(n(r));
        var v = t[1];
        return v && !a && e(t) && [ t ];
      }
    };
    var c = function intersectionObserverCallback(r, a) {
      return u(a.pop(), r);
    };
    return [ function() {
      var a = [];
      if (_) {
        t = new _(or(c, false), {
          root: r
        });
        t.observe(v);
        L(a, (function() {
          t.disconnect();
        }));
      } else {
        var e = function onSizeChanged() {
          var r = Qr(v);
          u(r);
        };
        L(a, $e(v, e)());
        e();
      }
      return or(N, L(a, Fr(r, v)));
    }, function() {
      return t && c(true, t.takeRecords());
    } ];
  };
  var Ke = function createObserversSetup(r, e, t, n) {
    var v;
    var i;
    var o;
    var u;
    var c;
    var l;
    var f = "[" + za + "]";
    var s = "[" + Ia + "]";
    var d = [ "id", "class", "style", "open", "wrap", "cols", "rows" ];
    var p = r.gr, _ = r.hr, h = r.tr, b = r.br, m = r.mr, S = r.er, w = r.Sr, O = r.yr, E = r.wr, A = r.Or;
    var H = function getDirectionIsRTL(r) {
      return getStyles(r, "direction") === "rtl";
    };
    var P = {
      Cr: false,
      ir: H(p)
    };
    var T = be();
    var D = Me(We);
    var z = a({
      i: tr,
      v: {
        w: 0,
        h: 0
      }
    }, (function() {
      var a = D && D.ar(r, e, P, T, t).sr;
      var n = w && S;
      var v = !n && xr(_, za, Ta);
      var i = !S && O(ka);
      var o = i && da(b);
      var u = o && A();
      var c = E(Ra, v);
      var l = i && a && a()[0];
      var f = aa(h);
      var s = ea(h);
      l && l();
      sa(b, o);
      u && u();
      v && c();
      return {
        w: f.w + s.w,
        h: f.h + s.h
      };
    })), M = z[0];
    var L = cr(n, {
      u: function _timeout() {
        return v;
      },
      p: function _maxDelay() {
        return i;
      },
      m: function _mergeParams(r, a) {
        var e = r[0];
        var t = a[0];
        return [ I(fr(e), fr(t)).reduce((function(r, a) {
          r[a] = e[a] || t[a];
          return r;
        }), {}) ];
      }
    });
    var V = function setDirection(r) {
      var a = H(p);
      sr(r, {
        Er: l !== a
      });
      sr(P, {
        ir: a
      });
      l = a;
    };
    var k = function onTrinsicChanged(r, a) {
      var e = r[0], t = r[1];
      var v = {
        Ar: t
      };
      sr(P, {
        Cr: e
      });
      !a && n(v);
      return v;
    };
    var R = function onSizeChanged(r) {
      var a = r._r, e = r.pr;
      var t = a && !e;
      var v = !t && T.V ? L : n;
      var i = {
        _r: a || e,
        pr: e
      };
      V(i);
      v(i);
    };
    var F = function onContentMutation(r, a) {
      var e = M(), t = e[1];
      var v = {
        Hr: t
      };
      V(v);
      var i = r ? n : L;
      t && !a && i(v);
      return v;
    };
    var N = function onHostMutation(r, a, e) {
      var t = {
        Pr: a
      };
      V(t);
      if (a && !e) {
        L(t);
      }
      return t;
    };
    var j = m ? Je(_, k) : [], U = j[0], q = j[1];
    var B = !S && $e(_, R, {
      pr: true
    });
    var Y = He(_, false, N, {
      W: d,
      Y: d
    }), W = Y[0], X = Y[1];
    var Z = S && g && new g((function(r) {
      var a = r[r.length - 1].contentRect;
      R({
        _r: true,
        pr: ia(a, c)
      });
      c = a;
    }));
    var G = cr((function() {
      var r = M(), a = r[1];
      n({
        Hr: a
      });
    }), {
      u: 222,
      _: true
    });
    return [ function() {
      Z && Z.observe(_);
      var r = B && B();
      var a = U && U();
      var e = W();
      var t = T.R((function(r) {
        if (r) {
          L({
            Tr: r
          });
        } else {
          G();
        }
      }));
      return function() {
        Z && Z.disconnect();
        r && r();
        a && a();
        u && u();
        e();
        t();
      };
    }, function(r) {
      var a = r.Dr, e = r.zr, t = r.Mr;
      var n = {};
      var c = a("update.ignoreMutation"), l = c[0];
      var p = a("update.attributes"), _ = p[0], g = p[1];
      var b = a("update.elementEvents"), w = b[0], O = b[1];
      var E = a("update.debounce"), A = E[0], H = E[1];
      var P = O || g;
      var T = e || t;
      var D = function ignoreMutationFromOptions(r) {
        return C(l) && l(r);
      };
      if (P) {
        o && o();
        u && u();
        var z = He(m || h, true, F, {
          Y: I(d, _ || []),
          X: w,
          Z: f,
          $: function _ignoreContentChange(r, a) {
            var e = r.target, t = r.attributeName;
            var n = !a && t && !S ? kr(e, f, s) : false;
            return n || !!Lr(e, "." + Ka) || !!D(r);
          }
        }), M = z[0], R = z[1];
        u = M();
        o = R;
      }
      if (H) {
        L.S();
        if (x(A)) {
          var j = A[0];
          var U = A[1];
          v = y(j) && j;
          i = y(U) && U;
        } else if (y(A)) {
          v = A;
          i = false;
        } else {
          v = false;
          i = false;
        }
      }
      if (T) {
        var B = X();
        var Y = q && q();
        var W = o && o();
        B && sr(n, N(B[0], B[1], T));
        Y && sr(n, k(Y[0], T));
        W && sr(n, F(W[0], T));
      }
      V(n);
      return n;
    }, P ];
  };
  var Qe = function createScrollbarsSetupElements(r, a, e, t) {
    var n = "--os-viewport-percent";
    var v = "--os-scroll-percent";
    var i = "--os-scroll-direction";
    var o = be(), u = o.F;
    var c = u(), l = c.scrollbars;
    var f = l.slot;
    var s = a.gr, d = a.hr, p = a.tr, _ = a.Ir, g = a.br, b = a.Sr, m = a.er;
    var S = _ ? {} : r, y = S.scrollbars;
    var w = y || {}, C = w.slot;
    var x = [];
    var E = [];
    var A = [];
    var H = ye([ s, d, p ], (function() {
      return m && b ? s : d;
    }), f, C);
    var P = function initScrollTimeline(r) {
      if (h) {
        var a = new h({
          source: g,
          axis: r
        });
        var e = function _addScrollPercentAnimation(r) {
          var e;
          var t = r.Lr.animate((e = {
            clear: [ "left" ]
          }, e[v] = [ 0, 1 ], e), {
            timeline: a
          });
          return function() {
            return t.cancel();
          };
        };
        return {
          Vr: e
        };
      }
    };
    var T = {
      x: P("x"),
      y: P("y")
    };
    var D = function getViewportPercent() {
      var r = e.kr, a = e.Rr;
      var t = function getAxisValue(r, a) {
        return _r(0, 1, r / (r + a) || 0);
      };
      return {
        x: t(a.x, r.x),
        y: t(a.y, r.y)
      };
    };
    var z = function scrollbarStructureAddRemoveClass(r, a, e) {
      var t = e ? Hr : Ar;
      each(r, (function(r) {
        t(r.Lr, a);
      }));
    };
    var M = function scrollbarStyle(r, a) {
      each(r, (function(r) {
        var e = a(r), t = e[0], n = e[1];
        setStyles(t, n);
      }));
    };
    var I = function scrollbarsAddRemoveClass(r, a, e) {
      var t = O(e);
      var n = t ? e : true;
      var v = t ? !e : true;
      n && z(E, r, a);
      v && z(A, r, a);
    };
    var V = function refreshScrollbarsHandleLength() {
      var r = D();
      var a = function createScrollbarStyleFn(r) {
        return function(a) {
          var e;
          return [ a.Lr, (e = {}, e[n] = Yr(r) + "", e) ];
        };
      };
      M(E, a(r.x));
      M(A, a(r.y));
    };
    var k = function refreshScrollbarsHandleOffset() {
      if (!h) {
        var r = e.Fr;
        var a = ha(r, da(g));
        var t = function createScrollbarStyleFn(r) {
          return function(a) {
            var e;
            return [ a.Lr, (e = {}, e[v] = Yr(r) + "", e) ];
          };
        };
        M(E, t(a.x));
        M(A, t(a.y));
      }
    };
    var R = function refreshScrollbarsScrollCoordinates() {
      var r = e.Fr;
      var a = ga(r);
      var t = function createScrollbarStyleFn(r) {
        return function(a) {
          var e;
          return [ a.Lr, (e = {}, e[i] = r ? "0" : "1", e) ];
        };
      };
      M(E, t(a.x));
      M(A, t(a.y));
    };
    var F = function refreshScrollbarsScrollbarOffset() {
      if (m && !b) {
        var r = e.kr, a = e.Fr;
        var t = ga(a);
        var n = ha(a, da(g));
        var v = function styleScrollbarPosition(a) {
          var e = a.Lr;
          var v = Ir(e) === p && e;
          var i = function getTranslateValue(r, a, e) {
            var t = a * r;
            return Wr(e ? t : -t);
          };
          return [ v, v && {
            transform: Zr({
              x: i(n.x, r.x, t.x),
              y: i(n.y, r.y, t.y)
            })
          } ];
        };
        M(E, v);
        M(A, v);
      }
    };
    var j = function generateScrollbarDOM(r) {
      var a = r ? "x" : "y";
      var e = r ? re : ae;
      var n = Nr(Ka + " " + e);
      var v = Nr(ee);
      var i = Nr(te);
      var o = {
        Lr: n,
        Nr: v,
        jr: i
      };
      var u = T[a];
      L(r ? E : A, o);
      L(x, [ Fr(n, v), Fr(v, i), or(Rr, n), u && u.Vr(o), t(o, I, r) ]);
      return o;
    };
    var U = or(j, true);
    var q = or(j, false);
    var B = function appendElements() {
      Fr(H, E[0].Lr);
      Fr(H, A[0].Lr);
      return or(N, x);
    };
    U();
    q();
    return [ {
      Ur: V,
      qr: k,
      Br: R,
      Yr: F,
      Wr: I,
      Xr: {
        Zr: E,
        Gr: U,
        $r: or(M, E)
      },
      Jr: {
        Zr: A,
        Gr: q,
        $r: or(M, A)
      }
    }, B ];
  };
  var rt = function createScrollbarsSetupEvents(r, a, e, t) {
    return function(n, v, u) {
      var c = a.hr, l = a.tr, s = a.er, d = a.br, p = a.Kr, _ = a.Or;
      var g = n.Lr, h = n.Nr, b = n.jr;
      var m = ur(333), S = m[0], y = m[1];
      var w = ur(444), O = w[0], x = w[1];
      var E = function scrollOffsetElementScrollBy(r) {
        C(d.scrollBy) && d.scrollBy({
          behavior: "smooth",
          left: r.x,
          top: r.y
        });
      };
      var A = function createInteractiveScrollEvents() {
        var a = "pointerup pointercancel lostpointercapture";
        var t = "client" + (u ? "X" : "Y");
        var n = u ? $ : J;
        var v = u ? "left" : "top";
        var c = u ? "w" : "h";
        var l = u ? "x" : "y";
        var f = function createRelativeHandleMove(r, a) {
          return function(t) {
            var n;
            var v = e.kr;
            var i = Qr(h)[c] - Qr(b)[c];
            var o = a * t / i;
            var u = o * v[l];
            sa(d, (n = {}, n[l] = r + u, n));
          };
        };
        var s = [];
        return ua(h, "pointerdown", (function(e) {
          var u = Lr(e.target, "." + te) === b;
          var g = u ? b : h;
          var m = r.scrollbars;
          var S = m[u ? "dragScroll" : "clickScroll"];
          var y = e.button, w = e.isPrimary, C = e.pointerType;
          var A = m.pointers;
          var H = y === 0 && w && S && (A || []).includes(C);
          if (H) {
            N(s);
            x();
            var P = !u && (e.shiftKey || S === "instant");
            var T = or(ta, b);
            var D = or(ta, h);
            var z = function getHandleOffset(r, a) {
              return (r || T())[v] - (a || D())[v];
            };
            var M = i(ta(d)[n]) / Qr(d)[c] || 1;
            var I = f(da(d)[l], 1 / M);
            var V = e[t];
            var k = T();
            var R = D();
            var F = k[n];
            var j = z(k, R) + F / 2;
            var U = V - R[v];
            var q = u ? 0 : U - j;
            var B = function releasePointerCapture(r) {
              N(X);
              g.releasePointerCapture(r.pointerId);
            };
            var Y = u || P;
            var W = _();
            var X = [ ua(p, a, B), ua(p, "selectstart", (function(r) {
              return la(r);
            }), {
              H: false
            }), ua(h, a, B), Y && ua(h, "pointermove", (function(r) {
              return I(q + (r[t] - V));
            })), Y && function() {
              var r = da(d);
              W();
              var a = da(d);
              var e = {
                x: a.x - r.x,
                y: a.y - r.y
              };
              if (o(e.x) > 3 || o(e.y) > 3) {
                _();
                sa(d, r);
                E(e);
                O(W);
              }
            } ];
            g.setPointerCapture(e.pointerId);
            if (P) {
              I(q);
            } else if (!u) {
              var Z = Me(Ze);
              if (Z) {
                var G = Z(I, q, F, (function(r) {
                  if (r) {
                    W();
                  } else {
                    L(X, W);
                  }
                }));
                L(X, G);
                L(s, or(G, true));
              }
            }
          }
        }));
      };
      var H = true;
      return or(N, [ ua(b, "pointermove pointerleave", t), ua(g, "pointerenter", (function() {
        v(ie, true);
      })), ua(g, "pointerleave pointercancel", (function() {
        v(ie, false);
      })), !s && ua(g, "mousedown", (function() {
        var r = Vr();
        if (br(r, Ia) || br(r, za) || r === document.body) {
          f(or(ba, l), 25);
        }
      })), ua(g, "wheel", (function(r) {
        var a = r.deltaX, e = r.deltaY, t = r.deltaMode;
        if (H && t === 0 && Ir(g) === c) {
          E({
            x: a,
            y: e
          });
        }
        H = false;
        v(le, true);
        S((function() {
          H = true;
          v(le);
        }));
        la(r);
      }), {
        H: false,
        P: true
      }), ua(g, "pointerdown", or(ua, p, "click", fa, {
        T: true,
        P: true,
        H: false
      }), {
        P: true
      }), A(), y, x ]);
    };
  };
  var at = function createScrollbarsSetup(r, a, e, t, n, v) {
    var i;
    var o;
    var u;
    var c;
    var l;
    var f = ir;
    var s = 0;
    var d = function isHoverablePointerType(r) {
      return r.pointerType === "mouse";
    };
    var p = ur(), _ = p[0], g = p[1];
    var h = ur(100), b = h[0], m = h[1];
    var S = ur(100), y = S[0], w = S[1];
    var O = ur((function() {
      return s;
    })), C = O[0], x = O[1];
    var E = Qe(r, n, t, rt(a, n, t, (function(r) {
      return d(r) && F();
    }))), A = E[0], H = E[1];
    var P = n.hr, T = n.Qr, D = n.Sr;
    var z = A.Wr, M = A.Ur, I = A.qr, V = A.Br, k = A.Yr;
    var R = function manageScrollbarsAutoHide(r, a) {
      x();
      if (r) {
        z(ce);
      } else {
        var e = or(z, ce, true);
        if (s > 0 && !a) {
          C(e);
        } else {
          e();
        }
      }
    };
    var F = function manageScrollbarsAutoHideInstantInteraction() {
      if (u ? !i : !c) {
        R(true);
        b((function() {
          R(false);
        }));
      }
    };
    var j = function manageAutoHideSuspension(r) {
      z(ue, r, true);
      z(ue, r, false);
    };
    var U = function onHostMouseEnter(r) {
      if (d(r)) {
        i = u;
        u && R(true);
      }
    };
    var q = [ x, m, w, g, function() {
      return f();
    }, ua(P, "pointerover", U, {
      T: true
    }), ua(P, "pointerenter", U), ua(P, "pointerleave", (function(r) {
      if (d(r)) {
        i = false;
        u && R(false);
      }
    })), ua(P, "pointermove", (function(r) {
      d(r) && o && F();
    })), ua(T, "scroll", (function(r) {
      _((function() {
        I();
        F();
      }));
      v(r);
      k();
    })) ];
    return [ function() {
      return or(N, L(q, H()));
    }, function(r) {
      var a = r.Dr, n = r.Mr, v = r.ra, i = r.aa;
      var d = i || {}, p = d.ea, _ = d.ta, g = d.na, h = d.va;
      var b = v || {}, m = b.Er, S = b.pr;
      var w = e.ir;
      var O = be(), C = O.L;
      var x = t.K, E = t.ia;
      var A = a("showNativeOverlaidScrollbars"), H = A[0], P = A[1];
      var L = a("scrollbars.theme"), F = L[0], N = L[1];
      var U = a("scrollbars.visibility"), q = U[0], B = U[1];
      var Y = a("scrollbars.autoHide"), W = Y[0], X = Y[1];
      var Z = a("scrollbars.autoHideSuspend"), G = Z[0], $ = Z[1];
      var J = a("scrollbars.autoHideDelay"), Q = J[0];
      var ar = a("scrollbars.dragScroll"), er = ar[0], tr = ar[1];
      var nr = a("scrollbars.clickScroll"), vr = nr[0], ir = nr[1];
      var ur = a("overflow"), cr = ur[0], lr = ur[1];
      var fr = S && !n;
      var sr = E.x || E.y;
      var dr = p || _ || h || m || n;
      var pr = g || B || lr;
      var _r = H && C.x && C.y;
      var gr = function setScrollbarVisibility(r, a, e) {
        var t = r.includes(rr) && (q === K || q === "auto" && a === rr);
        z(ne, t, e);
        return t;
      };
      s = Q;
      if (fr) {
        if (G && sr) {
          j(false);
          f();
          y((function() {
            f = ua(T, "scroll", or(j, true), {
              T: true
            });
          }));
        } else {
          j(true);
        }
      }
      if (P) {
        z(Ja, _r);
      }
      if (N) {
        z(l);
        z(F, true);
        l = F;
      }
      if ($ && !G) {
        j(true);
      }
      if (X) {
        o = W === "move";
        u = W === "leave";
        c = W === "never";
        R(c, true);
      }
      if (tr) {
        z(se, er);
      }
      if (ir) {
        z(fe, !!vr);
      }
      if (pr) {
        var hr = gr(cr.x, x.x, true);
        var br = gr(cr.y, x.y, false);
        var mr = hr && br;
        z(ve, !mr);
      }
      if (dr) {
        I();
        M();
        k();
        h && V();
        z(oe, !E.x, true);
        z(oe, !E.y, false);
        z(Qa, w && !D);
      }
    }, {}, A ];
  };
  var et = function createStructureSetupElements(r) {
    var a = be();
    var e = a.F, n = a.V;
    var v = e(), i = v.elements;
    var o = i.padding, u = i.viewport, c = i.content;
    var l = P(r);
    var f = l ? {} : r;
    var s = f.elements;
    var d = s || {}, p = d.padding, _ = d.viewport, g = d.content;
    var h = l ? r : f.target;
    var b = zr(h);
    var m = h.ownerDocument;
    var S = m.documentElement;
    var y = function getDocumentWindow() {
      return m.defaultView || t;
    };
    var w = or(Se, [ h ]);
    var O = or(ye, [ h ]);
    var C = or(Nr, "");
    var x = or(w, C, u);
    var E = or(O, C, c);
    var A = function elementHasOverflow(r) {
      var a = Qr(r);
      var e = aa(r);
      var t = getStyles(r, Z);
      var n = getStyles(r, G);
      return e.w - a.w > 0 && !Be(t) || e.h - a.h > 0 && !Be(n);
    };
    var H = x(_);
    var T = H === h;
    var D = T && b;
    var z = !T && E(g);
    var I = !T && H === z;
    var V = D ? S : H;
    var k = D ? V : h;
    var R = !T && O(C, o, p);
    var F = !I && z;
    var j = [ F, V, R, k ].map((function(r) {
      return P(r) && !Ir(r) && r;
    }));
    var U = function elementIsGenerated(r) {
      return r && M(j, r);
    };
    var q = !U(V) && A(V) ? V : h;
    var B = D ? S : V;
    var Y = D ? m : V;
    var W = {
      gr: h,
      hr: k,
      tr: V,
      oa: R,
      mr: F,
      br: B,
      Qr: Y,
      ua: b ? S : q,
      Kr: m,
      Sr: b,
      Ir: l,
      er: T,
      ca: y,
      yr: function _viewportHasClass(r) {
        return xr(V, Ia, r);
      },
      wr: function _viewportAddRemoveClass(r, a) {
        return Cr(V, Ia, r, a);
      },
      Or: function _removeScrollObscuringStyles() {
        return Cr(B, Ia, Fa, true);
      }
    };
    var X = W.gr, $ = W.hr, J = W.oa, K = W.tr, Q = W.mr;
    var rr = [ function() {
      Sr($, [ za, Pa ]);
      Sr(X, Pa);
      if (b) {
        Sr(S, [ Pa, za ]);
      }
    } ];
    var ar = Mr([ Q, K, J, $, X ].find((function(r) {
      return r && !U(r);
    })));
    var er = D ? X : Q || K;
    var tr = or(N, rr);
    var nr = function appendElements() {
      var r = y();
      var a = Vr();
      var e = function unwrap(r) {
        Fr(Ir(r), Mr(r));
        Rr(r);
      };
      var t = function prepareWrapUnwrapFocus(r) {
        return ua(r, "focusin focusout focus blur", fa, {
          P: true,
          H: false
        });
      };
      var v = "tabindex";
      var i = hr(K, v);
      var o = t(a);
      mr($, za, T ? "" : Ma);
      mr(J, Ua, "");
      mr(K, Ia, "");
      mr(Q, qa, "");
      if (!T) {
        mr(K, v, i || "-1");
        b && mr(S, Da, "");
      }
      Fr(er, ar);
      Fr($, J);
      Fr(J || $, !T && K);
      Fr(K, Q);
      L(rr, [ o, function() {
        var r = Vr();
        var a = U(K);
        var n = a && r === K ? X : r;
        var o = t(n);
        Sr(J, Ua);
        Sr(Q, qa);
        Sr(K, Ia);
        b && Sr(S, Da);
        i ? mr(K, v, i) : Sr(K, v);
        U(Q) && e(Q);
        a && e(K);
        U(J) && e(J);
        ba(n);
        o();
      } ]);
      if (n && !T) {
        Or(K, Ia, Na);
        L(rr, or(Sr, K, Ia));
      }
      ba(!T && b && a === X && r.top === r ? K : a);
      o();
      ar = 0;
      return tr;
    };
    return [ W, nr, tr ];
  };
  var tt = function createTrinsicUpdateSegment(r) {
    var a = r.mr;
    return function(r) {
      var e = r.ra, t = r.la, n = r.Mr;
      var v = e || {}, i = v.Ar;
      var o = t.Cr;
      var u = a && (i || n);
      if (u) {
        var c;
        setStyles(a, (c = {}, c[J] = o && "100%", c));
      }
    };
  };
  var nt = function createPaddingUpdateSegment(r, e) {
    var t = r.hr, n = r.oa, v = r.tr, i = r.er;
    var o = a({
      i: vr,
      v: Xr()
    }, or(Xr, t, "padding", "")), u = o[0], c = o[1];
    return function(r) {
      var a = r.Dr, t = r.ra, o = r.la, l = r.Mr;
      var f = c(l), s = f[0], d = f[1];
      var p = be(), _ = p.V;
      var g = t || {}, h = g._r, b = g.Hr, m = g.Er;
      var S = o.ir;
      var y = a("paddingAbsolute"), w = y[0], O = y[1];
      var C = l || b;
      if (h || d || C) {
        var x = u(l);
        s = x[0];
        d = x[1];
      }
      var E = !i && (O || m || d);
      if (E) {
        var A, H;
        var P = !w || !n && !_;
        var T = s.r + s.l;
        var D = s.t + s.b;
        var z = (A = {}, A[W] = P && !S ? -T : 0, A[X] = P ? -D : 0, A[Y] = P && S ? -T : 0, 
        A.top = P ? -s.t : 0, A.right = P ? S ? -s.r : "auto" : 0, A.left = P ? S ? "auto" : -s.l : 0, 
        A[$] = P && "calc(100% + " + T + "px)", A);
        var M = (H = {}, H[j] = P ? s.t : 0, H[U] = P ? s.r : 0, H[B] = P ? s.b : 0, H[q] = P ? s.l : 0, 
        H);
        setStyles(n || v, z);
        setStyles(v, M);
        sr(e, {
          oa: s,
          fa: !P,
          ur: n ? M : sr({}, z, M)
        });
      }
      return {
        sa: E
      };
    };
  };
  var vt = function createOverflowUpdateSegment(r, e) {
    var v = be();
    var i = r.hr, o = r.oa, u = r.tr, c = r.er, f = r.Qr, s = r.br, d = r.Sr, p = r.wr, _ = r.ca;
    var g = v.V;
    var h = d && c;
    var b = or(n, 0);
    var m = {
      display: function display() {
        return false;
      },
      direction: function direction(r) {
        return r !== "ltr";
      },
      flexDirection: function flexDirection(r) {
        return r.endsWith("-reverse");
      },
      writingMode: function writingMode(r) {
        return r !== "horizontal-tb";
      }
    };
    var S = fr(m);
    var y = {
      i: tr,
      v: {
        w: 0,
        h: 0
      }
    };
    var w = {
      i: nr,
      v: {}
    };
    var O = function setMeasuringMode(r) {
      p(Ra, !h && r);
    };
    var C = function getMeasuredScrollCoordinates(r) {
      var a = S.some((function(a) {
        var e = r[a];
        return e && m[a](e);
      }));
      if (!a) {
        return {
          D: {
            x: 0,
            y: 0
          },
          M: {
            x: 1,
            y: 1
          }
        };
      }
      O(true);
      var e = da(s);
      var t = p(ja, true);
      var n = ua(f, rr, (function(r) {
        var a = da(s);
        if (r.isTrusted && a.x === e.x && a.y === e.y) {
          ca(r);
        }
      }), {
        P: true,
        T: true
      });
      sa(s, {
        x: 0,
        y: 0
      });
      t();
      var v = da(s);
      var i = aa(s);
      sa(s, {
        x: i.w,
        y: i.h
      });
      var o = da(s);
      sa(s, {
        x: o.x - v.x < 1 && -i.w,
        y: o.y - v.y < 1 && -i.h
      });
      var u = da(s);
      sa(s, e);
      l((function() {
        return n();
      }));
      return {
        D: v,
        M: u
      };
    };
    var x = function getOverflowAmount(r, a) {
      var e = t.devicePixelRatio % 1 !== 0 ? 1 : 0;
      var n = {
        w: b(r.w - a.w),
        h: b(r.h - a.h)
      };
      return {
        w: n.w > e ? n.w : 0,
        h: n.h > e ? n.h : 0
      };
    };
    var E = a(y, or(ea, u)), A = E[0], H = E[1];
    var P = a(y, or(aa, u)), T = P[0], D = P[1];
    var z = a(y), M = z[0], I = z[1];
    var L = a(w), V = L[0];
    var k = a(y), R = k[0], F = k[1];
    var N = a(w), j = N[0];
    var U = a({
      i: function _equal(r, a) {
        return er(r, a, S);
      },
      v: {}
    }, (function() {
      return na(u) ? getStyles(u, S) : {};
    })), q = U[0];
    var B = a({
      i: function _equal(r, a) {
        return nr(r.D, a.D) && nr(r.M, a.M);
      },
      v: pa()
    }), Y = B[0], W = B[1];
    var X = Me(We);
    var Z = function createViewportOverflowStyleClassName(r, a) {
      var e = a ? La : Va;
      return "" + e + ar(r);
    };
    var G = function setViewportOverflowStyle(r) {
      var a = function createAllOverflowStyleClassNames(r) {
        return [ K, Q, rr ].map((function(a) {
          return Z(a, r);
        }));
      };
      var e = a(true).concat(a()).join(" ");
      p(e);
      p(fr(r).map((function(a) {
        return Z(r[a], a === "x");
      })).join(" "), true);
    };
    return function(a, t) {
      var n = a.Dr, c = a.ra, l = a.la, f = a.Mr;
      var s = t.sa;
      var d = c || {}, m = d.Er, S = d.pr, y = d.Tr;
      var w = X && X.ar(r, e, l, v, n);
      var E = w || {}, P = E.lr, z = E.sr, L = E.dr;
      var k = qe(n, v), N = k[0], U = k[1];
      var B = n("overflow"), Z = B[0], $ = B[1];
      var J = Be(Z.x);
      var K = Be(Z.y);
      var Q = true;
      var rr = H(f);
      var ar = D(f);
      var er = I(f);
      var tr = F(f);
      if (U && g) {
        p(Na, !N);
      }
      if (xr(i, za, Ta)) {
        O(true);
      }
      var nr = z ? z() : [], vr = nr[0];
      var ir = rr = A(f), or = ir[0];
      var ur = ar = T(f), cr = ur[0];
      var lr = ra(u);
      var fr = h && Kr(_());
      var dr = {
        w: b(cr.w + or.w),
        h: b(cr.h + or.h)
      };
      var pr = {
        w: b((fr ? fr.w : lr.w + b(lr.w - cr.w)) + or.w),
        h: b((fr ? fr.h : lr.h + b(lr.h - cr.h)) + or.h)
      };
      vr && vr();
      tr = R(pr);
      er = M(x(dr, pr), f);
      var _r = tr, gr = _r[0], hr = _r[1];
      var br = er, mr = br[0], Sr = br[1];
      var yr = ar, wr = yr[0], Or = yr[1];
      var Er = rr, Ar = Er[0], Hr = Er[1];
      var Pr = V({
        x: mr.w > 0,
        y: mr.h > 0
      }), Tr = Pr[0], Dr = Pr[1];
      var zr = J && K && (Tr.x || Tr.y) || J && Tr.x && !Tr.y || K && Tr.y && !Tr.x;
      var Mr = s || m || y || Hr || Or || hr || Sr || $ || U || Q;
      var Ir = Ye(Tr, Z);
      var Lr = j(Ir.K), Vr = Lr[0], kr = Lr[1];
      var Rr = q(f), Fr = Rr[0], Nr = Rr[1];
      var jr = m || S || Nr || Dr || f;
      var Ur = jr ? Y(C(Fr), f) : W(), qr = Ur[0], Br = Ur[1];
      if (Mr) {
        kr && G(Ir.K);
        if (L && P) {
          setStyles(u, L(Ir, l, P(Ir, wr, Ar)));
        }
      }
      O(false);
      Cr(i, za, Ta, zr);
      Cr(o, Ua, Ta, zr);
      sr(e, {
        K: Vr,
        Rr: {
          x: gr.w,
          y: gr.h
        },
        kr: {
          x: mr.w,
          y: mr.h
        },
        ia: Tr,
        Fr: _a(qr, mr)
      });
      return {
        na: kr,
        ea: hr,
        ta: Sr,
        va: Br || Sr,
        da: jr
      };
    };
  };
  var it = function createStructureSetup(r) {
    var a;
    var e = et(r), t = e[0], n = e[1], v = e[2];
    var i = {
      oa: {
        t: 0,
        r: 0,
        b: 0,
        l: 0
      },
      fa: false,
      ur: (a = {}, a[W] = 0, a[X] = 0, a[Y] = 0, a[j] = 0, a[U] = 0, a[B] = 0, a[q] = 0, 
      a),
      Rr: {
        x: 0,
        y: 0
      },
      kr: {
        x: 0,
        y: 0
      },
      K: {
        x: Q,
        y: Q
      },
      ia: {
        x: false,
        y: false
      },
      Fr: pa()
    };
    var o = t.gr, u = t.br, c = t.er, l = t.Or;
    var f = be(), s = f.V, d = f.L;
    var p = !s && (d.x || d.y);
    var _ = [ tt(t), nt(t, i), vt(t, i) ];
    return [ n, function(r) {
      var a = {};
      var e = p;
      var t = e && da(u);
      var n = t && l();
      each(_, (function(e) {
        sr(a, e(r, a) || {});
      }));
      sa(u, t);
      n && n();
      !c && sa(o, 0);
      return a;
    }, i, t, v ];
  };
  var ot = function createSetups(r, a, e, t, n) {
    var v = false;
    var i = xa(a, {});
    var o = it(r), u = o[0], c = o[1], l = o[2], f = o[3], s = o[4];
    var d = Ke(f, l, i, (function(r) {
      w({}, r);
    })), p = d[0], _ = d[1], g = d[2];
    var h = at(r, a, g, l, f, n), b = h[0], m = h[1], S = h[3];
    var y = function updateHintsAreTruthy(r) {
      return fr(r).some((function(a) {
        return !!r[a];
      }));
    };
    var w = function update(r, n) {
      if (e()) {
        return false;
      }
      var i = r.pa, o = r.Mr, u = r.zr, l = r._a;
      var f = i || {};
      var s = !!o || !v;
      var d = {
        Dr: xa(a, f, s),
        pa: f,
        Mr: s
      };
      if (l) {
        m(d);
        return false;
      }
      var p = n || _(sr({}, d, {
        zr: u
      }));
      var h = c(sr({}, d, {
        la: g,
        ra: p
      }));
      m(sr({}, d, {
        ra: p,
        aa: h
      }));
      var b = y(p);
      var S = y(h);
      var w = b || S || !pr(f) || s;
      v = true;
      w && t(r, {
        ra: p,
        aa: h
      });
      return w;
    };
    return [ function() {
      var r = f.ua, a = f.br, e = f.Or;
      var t = da(r);
      var n = [ p(), u(), b() ];
      var v = e();
      sa(a, t);
      v();
      return or(N, n);
    }, w, function() {
      return {
        ga: g,
        ha: l
      };
    }, {
      ba: f,
      ma: S
    }, s ];
  };
  var ut = function OverlayScrollbars(r, a, e) {
    var t = be(), n = t.j;
    var v = P(r);
    var i = v ? r : r.target;
    var o = Ee(i);
    if (a && !o) {
      var u = false;
      var c = [];
      var l = {};
      var f = function validateOptions(r) {
        var a = dr(r);
        var e = Me(Ne);
        return e ? e(a, true) : a;
      };
      var s = sr({}, n(), f(a));
      var d = Sa(), p = d[0], _ = d[1], g = d[2];
      var h = Sa(e), b = h[0], m = h[1], S = h[2];
      var y = function triggerEvent(r, a) {
        S(r, a);
        g(r, a);
      };
      var w = ot(r, s, (function() {
        return u;
      }), (function(r, a) {
        var e = r.pa, t = r.Mr;
        var n = a.ra, v = a.aa;
        var i = n._r, o = n.Er, u = n.Ar, c = n.Hr, l = n.Pr, f = n.pr;
        var s = v.ea, d = v.ta, p = v.na, _ = v.va;
        y("updated", [ T, {
          updateHints: {
            sizeChanged: !!i,
            directionChanged: !!o,
            heightIntrinsicChanged: !!u,
            overflowEdgeChanged: !!s,
            overflowAmountChanged: !!d,
            overflowStyleChanged: !!p,
            scrollCoordinatesChanged: !!_,
            contentMutation: !!c,
            hostMutation: !!l,
            appear: !!f
          },
          changedOptions: e || {},
          force: !!t
        } ]);
      }), (function(r) {
        return y("scroll", [ T, r ]);
      })), O = w[0], C = w[1], x = w[2], E = w[3], A = w[4];
      var H = function destroy(r) {
        xe(i);
        N(c);
        u = true;
        y("destroyed", [ T, r ]);
        _();
        m();
      };
      var T = {
        options: function options(r, a) {
          if (r) {
            var e = a ? n() : {};
            var t = Ca(s, sr(e, f(r)));
            if (!pr(t)) {
              sr(s, t);
              C({
                pa: t
              });
            }
          }
          return sr({}, s);
        },
        on: b,
        off: function off(r, a) {
          r && a && m(r, a);
        },
        state: function state() {
          var r = x(), a = r.ga, e = r.ha;
          var t = a.ir;
          var n = e.Rr, v = e.kr, i = e.K, o = e.ia, c = e.oa, l = e.fa, f = e.Fr;
          return sr({}, {
            overflowEdge: n,
            overflowAmount: v,
            overflowStyle: i,
            hasOverflow: o,
            scrollCoordinates: {
              start: f.D,
              end: f.M
            },
            padding: c,
            paddingAbsolute: l,
            directionRTL: t,
            destroyed: u
          });
        },
        elements: function elements() {
          var r = E.ba, a = r.gr, e = r.hr, t = r.oa, n = r.tr, v = r.mr, i = r.br, o = r.Qr;
          var u = E.ma, c = u.Xr, l = u.Jr;
          var f = function translateScrollbarStructure(r) {
            var a = r.jr, e = r.Nr, t = r.Lr;
            return {
              scrollbar: t,
              track: e,
              handle: a
            };
          };
          var s = function translateScrollbarsSetupElement(r) {
            var a = r.Zr, e = r.Gr;
            var t = f(a[0]);
            return sr({}, t, {
              clone: function clone() {
                var r = f(e());
                C({
                  _a: true
                });
                return r;
              }
            });
          };
          return sr({}, {
            target: a,
            host: e,
            padding: t || n,
            viewport: n,
            content: v || n,
            scrollOffsetElement: i,
            scrollEventElement: o,
            scrollbarHorizontal: s(c),
            scrollbarVertical: s(l)
          });
        },
        update: function update(r) {
          return C({
            Mr: r,
            zr: true
          });
        },
        destroy: or(H, false),
        plugin: function plugin(r) {
          return l[fr(r)[0]];
        }
      };
      L(c, [ A ]);
      Ce(i, T);
      ze(Pe, ut, [ T, p, l ]);
      if (we(E.ba.Sr, !v && r.cancel)) {
        H(true);
        return T;
      }
      L(c, O());
      y("initialized", [ T ]);
      T.update();
      return T;
    }
    return o;
  };
  ut.plugin = function(r) {
    var a = x(r);
    var e = a ? r : [ r ];
    var t = e.map((function(r) {
      return ze(r, ut)[0];
    }));
    De(e);
    return a ? t : t[0];
  };
  ut.valid = function(r) {
    var a = r && r.elements;
    var e = C(a) && a();
    return H(e) && !!Ee(e.target);
  };
  ut.env = function() {
    var r = be(), a = r.I, e = r.L, t = r.V, n = r.k, v = r.q, i = r.B, o = r.F, u = r.N, c = r.j, l = r.U;
    return sr({}, {
      scrollbarsSize: a,
      scrollbarsOverlaid: e,
      scrollbarsHiding: t,
      scrollTimeline: n,
      staticDefaultInitialization: v,
      staticDefaultOptions: i,
      getDefaultInitialization: o,
      setDefaultInitialization: u,
      getDefaultOptions: c,
      setDefaultOptions: l
    });
  };
  ut.nonce = _e;
  r.ClickScrollPlugin = Ge;
  r.OverlayScrollbars = ut;
  r.ScrollbarsHidingPlugin = Xe;
  r.SizeObserverPlugin = Ue;
  return r;
}({});
//# sourceMappingURL=overlayscrollbars.browser.es5.js.map
