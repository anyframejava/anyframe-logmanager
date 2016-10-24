/** 
 * <pre>
 * Date 함수에 format 문장을 추가한다.
 * - Date를 주어진 포멧의 문자열로 변환한다. 
 * - 참고 : http://www.codeproject.com/jscript/dateformat.asp
 * </pre>
 * @param fmt : 변환하기 위한 포멧 문자열
 * @return
 */
Date.prototype.format = function(fmt) {
    if (!this.valueOf()) return "";
 
    var dt = this;
    return fmt.replace(/(yyyy|yy|mm|dd|hh|mi|ss|am|pm)/gi,
        function($1){
            switch ($1){
                case 'yyyy': return dt.getFullYear();
                case 'yy':   return dt.getFullYear().toString().substr(2);
                case 'mm':   return 1 + dt.getMonth() > 9 ? 1 + dt.getMonth() : '0' + (1 + dt.getMonth()) ;
                case 'dd':   return dt.getDate() > 9 ? dt.getDate() : '0' + dt.getDate();
                case 'hh':   return dt.getHours() > 9 ? dt.getHours() : '0' + dt.getHours();
                case 'mi':   return dt.getMinutes() > 9 ? dt.getMinutes() : '0' + dt.getMinutes();
                case 'ss':   return dt.getSeconds() > 9 ? dt.getSeconds() : '0' + dt.getSeconds();
                case 'am':   return dt.getHours() < 12 ? 'am' : 'pm';
                case 'pm':   return dt.getHours() < 12 ? 'am' : 'pm';
            }
        } 
    );
};

/** 
 * <pre>
 * String 객체에 toDate 메소드를 추가
 * - 변환가능한 yyyy/mm/dd , yyyy-mm-dd 형태이다
 * </pre>
 * @return
 */
String.prototype.toDate = function () {
    var pt = /^(\d*)(-|\/|년)(\d*)(-|\/|월)(\d*)(일?)$/i;
    var cMatch = this.match(pt);

    if (cMatch.length < 6) return null;

    return new Date(cMatch[1], cMatch[3]-1, cMatch[5]);
};

/** 
 * <pre>
 * String 객체에 escapeHTML 메소드를 추가
 * html 형태의 데이타를 escape 처리
 * </pre>
 * @return
 */
String.prototype.escapeHTML = function(){
  return $('<pre>').text(this.toString()).remove().html();
};

/** 
 * <pre>
 * Map Utility
 * </pre>
 */
function Map() {
	this.map = new Object();
};
Map.prototype = {  
    put : function(key, value){  
        this.map[key] = value;
    },  
    get : function(key){  
        return this.map[key];
    },
    containsKey : function(key){   
     return key in this.map;
    },
    containsValue : function(value){   
     for(var prop in this.map){
      if(this.map[prop] == value) return true;
     }
     return false;
    },
    isEmpty : function(key){   
     return (this.size() == 0);
    },
    clear : function(){  
     for(var prop in this.map){
      delete this.map[prop];
     }
    },
    remove : function(key){   
     delete this.map[key];
    },
    keys : function(){  
        var keys = new Array();  
        for(var prop in this.map){  
            keys.push(prop);
        }  
        return keys;
    },
    values : function(){  
     var values = new Array();  
        for(var prop in this.map){  
         values.push(this.map[prop]);
        }  
        return values;
    },
    size : function(){
      var count = 0;
      for (var prop in this.map) {
        count++;
      }
      return count;
    }
};

var currentDate = new Date();

/**
 * @param d
 * @return
 */
function getDate(d) {
	return (new Date(Date.parse(currentDate) - d * 1000 * 60 * 60 * 24)).format('yyyy-mm-dd');
}

/**
 * @param t
 * @return
 */
function getTime(t) {
	return (new Date(Date.parse(currentDate) - 1000 * 60 * 60 * t)).format('yyyy-mm-dd,hh');
}

/**
 * @param sec
 * @return
 */
function getTimestamp(sec){
	return ((new Date(Date.parse(currentDate) - 1000 * sec ))).format('yyyy-mm-dd hh:mi:ss')
}

var REQUEST_CONTEXT = null;

/**
 * @param obj
 * @param options
 * @return
 */
function loading(obj, options) {
	if(typeof(options)=='undefined') { options = { caption:'<div style="margin-top:10px;">Loading...</div>', append:true, modal:true }; }
	if(typeof(options.caption)=='undefined') { options.caption = '<div style="margin-top:10px;">Loading...</div>'; }
	if(typeof(options.modal)=='undefined') { options.modal = true; }
	obj.append('<div id="mask"></div><div class="loading"><img src="' + REQUEST_CONTEXT + '/logmanager/images/loading.gif" /><div>' + options.caption + '</div></div>');
	if(options.modal) {
		obj.attr('caller','caller');
		$('#mask').attr('caller',obj.attr('caller'));
		if(obj.selector == 'body') {
			objW = $(window).width();
			objH = $(window).height();
		}else{
			$('#mask').height(obj.outerHeight(true));
			$('#mask').width(obj.outerWidth(true));
		}
		
		$('#mask').show();
	}
	
	var pos = obj.offset();
	var objW = obj.width();
	var objH = obj.height();
	var loadingW = obj.find('.loading').width();
	var loadingH = obj.find('.loading').height();
	//var axisX = pos.left + objW/2 - loadingW/2;
	//var axisY = pos.top + objH/2 + loadingH/2;
	var axisX = objW/2 - loadingW/2;
	var axisY = objH/2 + loadingH/2;
	
	if(axisY > 300) axisY -= 100;
	obj.find('.loading').css('position','absolute');
	obj.find('.loading').offset({top:axisY, left:axisX});
	obj.find('.loading').show();
}

/**
 * @param obj
 * @param finalize
 * @return
 */
function loadingClose(obj, finalize) {
	try {
		if(obj.attr('caller')==$('#mask').attr('caller')) {
			$('#mask').hide();
			$('#mask').remove();
		}
		obj.find('.loading').remove();
		if(finalize) finalize();
	} catch(e) {}
}

/**
 * 
 * @param object
 * @return
 */
JSON.stringify = JSON.stringify || function (obj) {
    var t = typeof (obj);
    if (t != "object" || obj === null) {
        // simple data type
        if (t == "string") obj = '"'+obj+'"';
        return String(obj);
    }
    else {
        // recurse array or object
        var n, v, json = [], arr = (obj && obj.constructor == Array);
        for (n in obj) {
            v = obj[n]; t = typeof(v);
            if (t == "string") v = '"'+v+'"';
            else if (t == "object" && v !== null) v = JSON.stringify(v);
            json.push((arr ? "" : '"' + n + '":') + String(v));
        }
        return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}");
    }
};


$(document).ready(function(e){
	if(REQUEST_CONTEXT == null) {
		REQUEST_CONTEXT = '/anyframe-logmanager-web/';
	}
});