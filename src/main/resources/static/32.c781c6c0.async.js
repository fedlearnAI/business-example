(window.webpackJsonp=window.webpackJsonp||[]).push([[32],{Rhu0:function(e,t,a){"use strict";a.r(t),a.d(t,"default",(function(){return L}));var n,r=a("pbKT"),i=a.n(r),o=(a("IzEo"),a("bx4M")),s=(a("g9YV"),a("wCAj")),c=(a("MXD1"),a("CFYs")),u=(a("5Dmo"),a("3S7+")),l=(a("+L6B"),a("2/Rp")),d=a("/HRN"),p=a.n(d),f=a("WaGi"),m=a.n(f),y=a("K47E"),v=a.n(y),h=a("N9n2"),k=a.n(h),g=a("ZDA2"),E=a.n(g),M=a("/+P4"),x=a.n(M),I=a("xHqa"),P=a.n(I),R=(a("y8nQ"),a("Vl3Y")),w=a("q1tI"),N=a.n(w),q=a("MuoO"),C=a("7DNP"),T=a("NTd/"),b=a.n(T);function D(e){var t=function(){if("undefined"==typeof Reflect||!i.a)return!1;if(i.a.sham)return!1;if("function"==typeof Proxy)return!0;try{return Boolean.prototype.valueOf.call(i()(Boolean,[],(function(){}))),!0}catch(e){return!1}}();return function(){var a,n=x()(e);if(t){var r=x()(this).constructor;a=i()(n,arguments,r)}else a=n.apply(this,arguments);return E()(this,a)}}var L=Object(q.connect)((function(e){var t=e.taskPrivacy,a=e.loading,n=e.user.currentUser.username;return{taskPrivacy:t,loading:a.models.taskPrivacy,username:n}}))(n=R.a.create({})(n=function(e){k()(a,e);var t=D(a);function a(){var e;p()(this,a);for(var n=arguments.length,r=new Array(n),i=0;i<n;i++)r[i]=arguments[i];return e=t.call.apply(t,[this].concat(r)),P()(v()(e),"interval",null),P()(v()(e),"handleRefresh",(function(){e.queryTaskList()})),P()(v()(e),"queryTaskList",(function(){var t=e.props;(0,t.dispatch)({type:"taskPrivacy/queryTaskList",payload:{username:t.username,category:"inference"}})})),e}return m()(a,[{key:"componentDidMount",value:function(){this.queryTaskList()}},{key:"render",value:function(){var e=this.props,t=e.lists,a=e.dispatch;return N.a.createElement(o.a,{title:b.a.formatMessage({id:"privacy.list"})},N.a.createElement("div",null,N.a.createElement(l.a,{icon:"undo",type:"primary",style:{marginRight:"20px"},onClick:this.handleRefresh},b.a.formatMessage({id:"total.refresh"})),N.a.createElement(l.a,{icon:"plus",type:"primary",onClick:function(){a(C.routerRedux.push("/task/privacy/add"))}},b.a.formatMessage({id:"privacy.create"}))),N.a.createElement("div",{style:{padding:"20px 0"}},N.a.createElement(s.a,{dataSource:t,columns:[{title:b.a.formatMessage({id:"total.taskId"}),dataIndex:"taskId"},{title:b.a.formatMessage({id:"total.taskName"}),dataIndex:"taskName",render:function(e,t,n){return N.a.createElement(u.a,{title:"".concat(b.a.formatMessage({id:"total.detail"}))},N.a.createElement("span",{className:"point",onClick:function(){return a(C.routerRedux.push("/task/privacy/detail?id=".concat(t.taskId)))}},e))}},{title:b.a.formatMessage({id:"total.progress"}),dataIndex:"percent",render:function(e,t,a){return N.a.createElement(c.a,{percent:e})}},{title:b.a.formatMessage({id:"total.operate"}),dataIndex:"operate",render:function(e,t,a){return N.a.createElement("div",null,N.a.createElement("span",{className:"point"},b.a.formatMessage({id:"total.end"})))}}]})))}}]),a}(w.PureComponent))||n)||n},t33a:function(e,t,a){e.exports=a("cHUP")(10)}}]);