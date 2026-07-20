package telecaps.common.cfg;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 어플리케이션 전체에 적용되는 필터이다.
 * 
 * <ul>
 * <li>encoding 파라미터 : encoding 파라미터를 설정하면 request 객체에
 * setCharacterEncoding(encoding)을 실행한다.</li>
 * <li>ajaxFlag 파라미터 : Ajax요청임을 나타내는 HTTP 파라미터 이름이다. ajaxFilter로 지정한 HTTP 파라미터의
 * 값이 true 로 설정되면 인코딩을 무조건 UTF-8로 설정한다.</li>
 * </ul>
 * 
 * @author 손권남(kwon37xi@yahoo.co.kr)
 * 
 */
public class EncodingFilter implements Filter {


    /** HTTP 요청 문자 인코딩 */
    private String encoding = null;

    /** Ajax 요청임을 나타내는 플래그 파라미터 이름 */
    private String ajaxFlag = null;

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if (ajaxFlag != null
                && "true".equals(((HttpServletRequest) request)
                        .getHeader(ajaxFlag))) {
            // Ajax 처리 요청일 경우 무조건 UTF-8 지정.
            request.setCharacterEncoding("UTF-8");
            
            System.out.println("EncodingFilter doFilter :요청 헤더에 " + ajaxFlag + "가 "
                        + ((HttpServletRequest) request).getHeader(ajaxFlag)
                        + "로 설정되어 있어 문자 인코딩에  UTF-8을 사용합니다.");
//            if (log.isDebugEnabled()) {
//                log.debug(");
//            }
        } else if (encoding != null) {
            // Ajax 플래그가 true가 아니면, 기본적인 인코딩을 적용한다.
            request.setCharacterEncoding(encoding);
            System.out.println("EncodingFilter doFilter :문자 인코딩에 " + encoding + "을 사용합니다.");
//            if (log.isDebugEnabled()) {
//                log.debug("문자 인코딩에 " + encoding + "을 사용합니다.");
//            }
        }

        chain.doFilter(request, response);
    }

    public void init(FilterConfig config) throws ServletException {
        encoding = config.getInitParameter("encoding");

        ajaxFlag = config.getInitParameter("ajaxFlag");
        System.out.println("EncodingFilter init :encoding : " + encoding + ", ajaxFlag : " + ajaxFlag);
//        if (log.isDebugEnabled()) {
//            log.info("encoding : " + encoding + ", ajaxFlag : " + ajaxFlag);
//        }
    }

    public void destroy() {
    }
}
