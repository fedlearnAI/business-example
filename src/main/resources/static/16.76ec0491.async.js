(window.webpackJsonp=window.webpackJsonp||[]).push([[16],{"0PTn":function(e,t,n){"use strict";n.r(t),n.d(t,"default",(function(){return te}));var a,r=n("hfKm"),i=n.n(r),o=n("2Eek"),u=n.n(o),l=n("XoMD"),c=n.n(l),s=n("Jo+v"),p=n.n(s),f=n("4mXO"),m=n.n(f),d=n("pLtp"),h=n.n(d),v=n("pbKT"),y=n.n(v),b=(n("IzEo"),n("bx4M")),E=(n("MXD1"),n("CFYs")),k=n("htGi"),g=n.n(k),T=(n("+L6B"),n("2/Rp")),P=(n("miYZ"),n("tsqr")),x=n("Cg2A"),w=n.n(x),C=n("/HRN"),B=n.n(C),M=n("WaGi"),N=n.n(M),O=n("K47E"),R=n.n(O),D=n("N9n2"),I=n.n(D),q=n("ZDA2"),A=n.n(q),j=n("/+P4"),V=n.n(j),z=n("xHqa"),H=n.n(z),L=(n("y8nQ"),n("Vl3Y")),S=n("q1tI"),G=n.n(S),K=n("MuoO"),U=n("NTd/"),F=n.n(U),J=n("7DNP"),W=n("Mlzr"),X=n("61Lz");function Y(e,t){var n=h()(e);if(m.a){var a=m()(e);t&&(a=a.filter((function(t){return p()(e,t).enumerable}))),n.push.apply(n,a)}return n}function Z(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?Y(Object(n),!0).forEach((function(t){H()(e,t,n[t])})):c.a?u()(e,c()(n)):Y(Object(n)).forEach((function(t){i()(e,t,p()(n,t))}))}return e}function Q(e){var t=function(){if("undefined"==typeof Reflect||!y.a)return!1;if(y.a.sham)return!1;if("function"==typeof Proxy)return!0;try{return Boolean.prototype.valueOf.call(y()(Boolean,[],(function(){}))),!0}catch(e){return!1}}();return function(){var n,a=V()(e);if(t){var r=V()(this).constructor;n=y()(a,arguments,r)}else n=a.apply(this,arguments);return A()(this,n)}}var $=L.a.Item,_={labelCol:{xs:{span:24},sm:{span:6}},wrapperCol:{xs:{span:24},sm:{span:12}},style:{textAlign:"left"}},ee=function(e){return F.a.formatMessage(e)},te=Object(K.connect)((function(e){return{taskTrain:e.taskTrain,username:e.user.currentUser.username}}))(a=L.a.create()(a=function(e){I()(n,e);var t=Q(n);function n(){var e;B()(this,n);for(var a=arguments.length,r=new Array(a),i=0;i<a;i++)r[i]=arguments[i];return e=t.call.apply(t,[this].concat(r)),H()(R()(e),"state",{isReset:!1,detail:{}}),H()(R()(e),"componentDidMount",(function(){var t=e.props,n=t.dispatch,a=t.username;n({type:"taskTrain/queryTrainParamter",payload:{trainId:Object(W.a)("token"),username:a}}).then((function(t){t&&(e.setState({detail:Z({},t)}),"running"===t.runningStatus.toLowerCase()&&e.updateProgress())}))})),H()(R()(e),"componentWillUnmount",(function(){var t=e.props.dispatch;e.timeout&&clearTimeout(e.timeout),t({type:"taskTrain/queryParam",payload:{param:[]}})})),H()(R()(e),"timeout",null),H()(R()(e),"calcTime",(function(e){var t=Math.floor((+w()()-+new Date(e))/1e3);return"".concat(Math.floor(t/60)||"00",":").concat(t%60||"00")})),H()(R()(e),"updateProgress",(function(){(0,e.props.dispatch)({type:"taskTrain/resolveTaskRunning",payload:{modelToken:Object(W.a)("token")}}).then((function(t){t&&0===t.code&&t.data&&(t.data?(e.setState((function(e){return{detail:Z(Z({},e.detail),{},{percent:t.data.percent,describes:t.data.describes})}}),(function(){})),e.timeout=setTimeout((function(){e.updateProgress()}),1e4)):(clearTimeout(e.timeout),P.a.error(ee({id:"trainFail"}),3)))}))})),e}return N()(n,[{key:"render",value:function(){var e=this.props,t=e.dispatch,n=e.form.getFieldDecorator,a=this.state.detail,r=a.modelToken,i=a.model,o=a.algorithmParams,u=a.trainStartTime,l=a.percent,c=a.trainInfo,s=a.describes;return G.a.createElement(b.a,{title:ee({id:"train.title"}),extra:G.a.createElement(T.a,{type:"primary",onClick:function(){t(J.routerRedux.push("/task/train/list"))}},ee({id:"launch.goback"}))},G.a.createElement(L.a,{className:"task-info-form"},G.a.createElement($,g()({label:ee({id:"total.taskName"})},_),n("taskId",{initialValue:Object(W.a)("taskName")})(G.a.createElement(X.a,{disabled:!0}))),G.a.createElement($,g()({label:"modelToken"},_),n("modelToken",{initialValue:r})(G.a.createElement(X.a,{disabled:!0}))),G.a.createElement($,g()({label:ee({id:"train.modal"})},_),n("model",{initialValue:i||""})(G.a.createElement(X.a,{disabled:!0}))),o&&o.map((function(e,t){var a=e.name,r=e.field,i=e.value;return G.a.createElement($,g()({label:a},_,{key:t}),n(r,{initialValue:i})(G.a.createElement(X.a,{disabled:!0})))}))||null,G.a.createElement($,g()({label:ee({id:"train.startTime"})},_),n("trainStartTime",{initialValue:u})(G.a.createElement(X.a,{disabled:!0}))),G.a.createElement($,g()({label:ee({id:"train.trainprogress"})},_),G.a.createElement(E.a,{percent:l})),G.a.createElement($,g()({label:ee({id:"train.info"})},_),G.a.createElement(b.a,null,c&&c.map((function(e){return G.a.createElement("p",null,e)}))||s&&s.map((function(e){return G.a.createElement("p",null,e)}))||null),G.a.createElement("span",null,"训练时间: ",this.calcTime(u)))))}}]),n}(S.PureComponent))||a)||a},"61Lz":function(e,t,n){"use strict";var a=n("pbKT"),r=n.n(a),i=(n("5NDa"),n("5rEg")),o=n("htGi"),u=n.n(o),l=n("/HRN"),c=n.n(l),s=n("WaGi"),p=n.n(s),f=n("N9n2"),m=n.n(f),d=n("ZDA2"),h=n.n(d),v=n("/+P4"),y=n.n(v),b=n("xHqa"),E=n.n(b),k=n("q1tI"),g=n.n(k);function T(e){var t=function(){if("undefined"==typeof Reflect||!r.a)return!1;if(r.a.sham)return!1;if("function"==typeof Proxy)return!0;try{return Boolean.prototype.valueOf.call(r()(Boolean,[],(function(){}))),!0}catch(e){return!1}}();return function(){var n,a=y()(e);if(t){var i=y()(this).constructor;n=r()(a,arguments,i)}else n=a.apply(this,arguments);return h()(this,n)}}var P=function(e){m()(n,e);var t=T(n);function n(){return c()(this,n),t.apply(this,arguments)}return p()(n,[{key:"render",value:function(){var e=this;return g.a.createElement(i.a.TextArea,u()({},this.props,{onBlur:function(t){t.target.value=(t.target.value||"").trim(),e.props.onChange(t),e.props.onBlur(t)}}))}}]),n}(k.PureComponent);E()(P,"defaultProps",{onChange:function(){},onBlur:function(){}});var x=function(e){m()(n,e);var t=T(n);function n(){return c()(this,n),t.apply(this,arguments)}return p()(n,[{key:"render",value:function(){var e=this;return g.a.createElement(i.a,u()({},this.props,{onBlur:function(t){t.target.value=(t.target.value||"").trim(),e.props.onChange(t),e.props.onBlur(t)}}))}}]),n}(k.PureComponent);E()(x,"defaultProps",{onChange:function(){},onBlur:function(){}}),x.TextArea=P,t.a=x},Mlzr:function(e,t,n){"use strict";function a(e){var t=new RegExp("(^|&|\\?)"+e+"=([^&]*)(&|$)"),n=window.location.href.match(t);return null!=n?decodeURI(n[2]):null}n.d(t,"a",(function(){return a}))},t33a:function(e,t,n){e.exports=n("cHUP")(10)}}]);