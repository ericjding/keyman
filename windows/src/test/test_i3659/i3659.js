KeymanWeb.KR(new Keyboard_i3659());function Keyboard_i3659(){this.KI="Keyboard_i3659";this.KN="I3659 - Fixup KSAVE etc with KeymanWeb compiler";this.KV=null;this.KH='';this.KM=0;this.s5=KeymanWeb.KLOAD(this.KI,"col","a");this.s9="a";this.s10="b";this.s11="b";this.s12="a";this.s13="z";this.s14="windows";this.s15="vista";this.s16="layer";this.KVER="9.0.421.0";this.gs=function(t,e){return this.g0(t,e);};this.g0=function(t,e){var k=KeymanWeb,r=0,m=0;if(k.KKM(e,16400,90)&&this.s5!==this.s13){r=m=1;k.KO(0,t,"boo!");}else if(k.KKM(e,16384,65)){r=m=1;this.s5=this.s12;k.KO(-1,t," set to <a> ");}else if(k.KKM(e,16384,66)){r=m=1;this.s5=this.s11;k.KO(-1,t," set to <b> ");}else if(k.KKM(e,16384,76)){r=m=1;k.KSETS(33,this.s16,t);}else if(k.KKM(e,16384,80)&&k.KIFS(31,this.s14,t)){r=m=1;k.KO(0,t,"win");}else if(k.KKM(e,16384,81)&&!k.KIFS(31,this.s15,t)){r=m=1;k.KO(0,t,"not vista");}else if(k.KKM(e,16384,88)){r=m=1;k.KSAVE("col",this.s5);k.KO(-1,t," saved ");}else if(k.KKM(e,16384,89)){r=m=1;this.s5=k.KLOAD(this.KI,"col","a");k.KO(-1,t," reset ");}else if(k.KKM(e,16384,90)&&this.s5===this.s9){r=m=1;k.KO(0,t," col is <a> ");}else if(k.KKM(e,16384,90)&&this.s5===this.s10){r=m=1;k.KO(0,t," col is <b> ");}else if(k.KKM(e,16384,90)){r=m=1;r=this.g1(t,e);}if(m){r=this.g1(t,e);}if(!m&&k.KIK(e)){r=1;r=this.g1(t,e);}return r;};this.g1=function(t,e){var k=KeymanWeb,r=1,m=0;if(k.KCM(1,t,"a",1)){m=1;k.KO(1,t,"b");}return r;};}