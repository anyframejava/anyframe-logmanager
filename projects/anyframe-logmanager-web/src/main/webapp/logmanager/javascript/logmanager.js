
// a global month names array
var gsMonthNames = new Array(
	'January',
	'February',
	'March',
	'April',
	'May',
	'June',
	'July',
	'August',
	'September',
	'October',
	'November',
	'December'
);

// a global day names array
var gsDayNames = new Array(
	'Sunday',
	'Monday',
	'Tuesday',
	'Wednesday',
	'Thursday',
	'Friday',
	'Saturday'
);

String.prototype.zf = function(l) { return '0'.string(l - this.length) + this; };
String.prototype.string = function(l) { var s = '', i = 0; while (i++ < l) { s += this; } return s; };
Number.prototype.zf = function(l) { return this.toString().zf(l); };

/** 
 * <pre>
 * the date format prototype
 * Date 함수에 format 문장을 추가한다.
 * - Date를 주어진 포멧의 문자열로 변환한다. 
 * - 참고 : http://www.codeproject.com/jscript/dateformat.asp
 * </pre>
 * @param fmt : 변환하기 위한 포멧 문자열
 * @return
 */
Date.prototype.format = function(fmt){
	if (!this.valueOf())
		return 'n.a.';//&nbsp;
 
	var d = this;
    return fmt.replace(/(yyyy|yy|y|MMMM|MMM|MM|M|dddd|ddd|dd|d|HH|H|hh|h|mm|m|ss|s|t|x|SSS)/g,
    	function($1){
    		switch ($1){
	          	case 'yyyy': return d.getFullYear();
	          	case 'yy':   return (d.getFullYear()%100).zf(2);
	          	case 'y':      return (d.getFullYear()%100);
	          	case 'MMMM': return gsMonthNames[d.getMonth()];
	          	case 'MMM':   return gsMonthNames[d.getMonth()].substr(0, 3);
	          	case 'MM':   return (d.getMonth() + 1).zf(2);
	          	case 'M':      return (d.getMonth() + 1);
	          	case 'dddd': return gsDayNames[d.getDay()];
	          	case 'ddd':   return gsDayNames[d.getDay()].substr(0, 3);
	          	case 'dd':   return d.getDate().zf(2);
	          	case 'd':     return d.getDate();
	          	case 'HH':   return d.getHours().zf(2);
	          	case 'H':      return d.getHours();
	          	case 'hh':   return ((h = d.getHours() % 12) ? h : 12).zf(2);
	          	case 'h':      return ((h = d.getHours() % 12) ? h : 12);
	          	case 'mm':   return d.getMinutes().zf(2);
	          	case 'm':      return d.getMinutes();
	          	case 'ss':   return d.getSeconds().zf(2);
	          	case 's':      return d.getSeconds();
	          	case 't':     return d.getHours() < 12 ? 'am' : 'pm';
	          	case 'SSS':     return d.getMilliseconds().zf(3);
	          	case 'x': var i = d.getDate() % 10; return 'thstndrd'.substr(2 * (i < 4) * i, 2);               
    		}               
    	}
    );
};

/**
 * millisecond 형태의 date를 format 하여 출력
 * @param f
 * @returns
 */
Number.prototype.dateFormat = function(fmt) {
	var d = new Date();
	d.setTime(this);
	return d.format(fmt);
};

/**
 * millisecond 형태의 date를 format 하여 출력
 * @param f
 * @returns
 */
String.prototype.dateFormat = function(fmt) {
	var d = new Date();
	d.setTime(parseInt(this));
	return d.format(fmt);
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
 * parse boolean
 * 
 * @param str
 * @returns
 */
function parseBoolean(str) {
  return /^true$/i.test(str);
}

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
	return (new Date(Date.parse(currentDate) - d * 1000 * 60 * 60 * 24)).format('yyyy-MM-dd');
}

/**
 * @param t
 * @return
 */
function getTime(t) {
	return (new Date(Date.parse(currentDate) - 1000 * 60 * 60 * t)).format('yyyy-MM-dd,HH');
}

/**
 * @param sec
 * @return
 */
function getTimestamp(sec){
	return ((new Date(Date.parse(currentDate) - 1000 * sec ))).format('yyyy-MM-dd HH:mm:ss')
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
	if(typeof(options.image)=='undefined') { options.image = true; }
	obj.append('<div id="mask"></div><div class="loading">' + (options.image ? '<img src="' + REQUEST_CONTEXT + '/logmanager/images/loading.gif" />' : '') + '<div>' + options.caption + '</div></div>');
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

// *************************************************************
// editor dialog plugin
// *************************************************************
var EditorDialogs = {};
var COMMANDs = ['options', 'open', 'close'];

(function($){

	/**
	 * editor dialog plugins
	 **/
	$.fn.editorDialog = function(params) {
		
		var args = this.editorDialog.arguments;
		var id = this.attr('id');
		if(args.length == 1 && typeof(params) == 'string' && COMMANDs.indexOf(params) == -1) { // options attribute getter
			var value = null;
			try{
				value = EditorDialogs[id].options[params];
			}catch(e) {
				value = null;
			}
			return value;
		}else{
			this.each(function(index) {
				var el = $(this);
				var id = el.attr('id');

				var options = {
					open : function() {
					},
					close : function() {
					},
					width : 600,
					height : 500,
					position : ['center', 120]
				};

				// parameter process
				if(args.length == 1) {
					if(typeof(params) == 'object') { // initialize
						$.extend(options, params);

						EditorDialogs[id] = {
							'options' : options,
							'body' : el
						};

						_render(EditorDialogs[id]);
						
						el.keydown(function(event) {
							if(event.keyCode == 27) {
								el.editorDialog('close');
							}
						});

					}else if(typeof(params) == 'string') { // command execute
						if(params == 'options') { // 'options' command
							var s = '';
							var opts = EditorDialogs[id].options;
							for(a in opts) {
								s += 'options.' + a + '=' + opts[a] + '\n';
							}
							alert(s);
						}else if(params == 'open') { // 'open' command
							EditorDialogs[id].options.open();
							el.show();
						}else if(params == 'close') { // 'close' command
							EditorDialogs[id].options.close();
							el.hide();
						}
					}
				}else if(args.length == 2) { // option attribute setter
					if(typeof(params) == 'string') {
						EditorDialogs[id].options[params] = args[1];
					}
				}else {
				}
			});
			
			return this;
		}
	};


})(jQuery);

function _render(params) {
	var options = params.options;
	var el = params.body;
	el.width(options.width);
	el.height(options.height);
	var left = '300px';
	var top = '120px';
	if(options.position[0] == 'center') {
		left = $(window).scrollLeft() + ($(window).width() - el.width()) / 2;
	}
	if(options.position[1] == 'center') {
		top = $(window).scrollTop() + ($(window).height() - el.height()) / 2;
	}
	el.css({
		'position' : 'absolute', 
		'top' : top, 
		'left' : left, 
		'z-index' : 1000, 
		'display' : 'none', 
		'border' : '1px solid #333',
		'background-color' : '#ffffff'});
}


