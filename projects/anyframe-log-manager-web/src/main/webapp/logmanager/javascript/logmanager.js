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
}

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
}

var currentDate = new Date();

function getDate(d) {
	return (new Date(Date.parse(currentDate) - d * 1000 * 60 * 60 * 24)).format('yyyy-mm-dd');
}

function getTime(t) {
	return (new Date(Date.parse(currentDate) - 1000 * 60 * 60 * t)).format('yyyy-mm-dd,hh');
}

function getTimestamp(sec){
	return ((new Date(Date.parse(currentDate) - 1000 * sec ))).format('yyyy-mm-dd hh:mi:ss')
}

$(document).ready(function(e){
	
});