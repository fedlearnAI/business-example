(window.webpackJsonp=window.webpackJsonp||[]).push([[42],{aFNs:function(t,n,a){"use strict";a.r(n),a.d(n,"default",(function(){return F}));var e,r=a("hfKm"),o=a.n(r),i=a("2Eek"),c=a.n(i),u=a("XoMD"),s=a.n(u),f=a("Jo+v"),l=a.n(f),p=a("4mXO"),h=a.n(p),d=a("pLtp"),v=a.n(d),y=a("pbKT"),k=a.n(y),m=(a("IzEo"),a("bx4M")),b=(a("+L6B"),a("2/Rp")),D=a("/HRN"),w=a.n(D),E=a("WaGi"),P=a.n(E),M=a("K47E"),N=a.n(M),O=a("N9n2"),g=a.n(O),x=a("ZDA2"),R=a.n(x),j=a("/+P4"),q=a.n(j),B=a("xHqa"),I=a.n(B),J=a("q1tI"),K=a.n(J),T=a("MuoO"),A=a("NTd/"),C=a.n(A),H=a("7DNP");function L(t,n){var a=v()(t);if(h.a){var e=h()(t);n&&(e=e.filter((function(n){return l()(t,n).enumerable}))),a.push.apply(a,e)}return a}function X(t){for(var n=1;n<arguments.length;n++){var a=null!=arguments[n]?arguments[n]:{};n%2?L(Object(a),!0).forEach((function(n){I()(t,n,a[n])})):s.a?c()(t,s()(a)):L(Object(a)).forEach((function(n){o()(t,n,l()(a,n))}))}return t}function z(t){var n=function(){if("undefined"==typeof Reflect||!k.a)return!1;if(k.a.sham)return!1;if("function"==typeof Proxy)return!0;try{return Boolean.prototype.valueOf.call(k()(Boolean,[],(function(){}))),!0}catch(t){return!1}}();return function(){var a,e=q()(t);if(n){var r=q()(this).constructor;a=k()(e,arguments,r)}else a=e.apply(this,arguments);return R()(this,a)}}var F=Object(T.connect)((function(t){return{taskPrivacy:t.taskPrivacy}}))(e=function(t){g()(a,t);var n=z(a);function a(){var t;w()(this,a);for(var e=arguments.length,r=new Array(e),o=0;o<e;o++)r[o]=arguments[o];return t=n.call.apply(n,[this].concat(r)),I()(N()(t),"state",{dataDetail:{}}),I()(N()(t),"componentDidMount",(function(){(0,t.props.dispatch)({type:"taskPrivacy/queryTaskDetail",payload:{taskId:location.search.split("=")[1]}}).then((function(n){n&&t.setState({dataDetail:X({},n)})}))})),t}return P()(a,[{key:"render",value:function(){var t=this.props.dispatch,n=this.state.dataDetail;return K.a.createElement(m.a,{title:"".concat(C.a.formatMessage({id:"total.detail"}),": ").concat(n.taskName||""),extra:K.a.createElement(b.a,{type:"primary",onClick:function(){t(H.routerRedux.push("/task/privacy/list"))}},C.a.formatMessage({id:"launch.goback"}))},"detail")}}]),a}(J.PureComponent))||e}}]);