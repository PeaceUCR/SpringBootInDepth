package com.example.demo.filter;

import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Scanner;

import org.springframework.mock.web.DelegatingServletOutputStream;

//https://stackoverflow.com/questions/4122870/what-is-the-use-of-filter-and-chain-in-servlet
@Component
public class LogFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("sample filter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // do stuff before servlet gets called

        MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest((HttpServletRequest) servletRequest);
        //HttpServletRequest req = (HttpServletRequest) servletRequest;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream ps = new PrintStream(baos);

        //https://stackoverflow.com/questions/4449096/how-to-read-request-getinputstream-multiple-times
        //if read inputstream here, then the error will be in controller,
        //request.getInputStream() is allowed to read only one time.
        //we need to print with out affect the origin request
        //catch stream and then get
        //https://stackoverflow.com/questions/35549040/failed-to-read-http-message-org-springframework-http-converter-httpmessagenotre
        //https://stackoverflow.com/questions/10210645/http-servlet-request-lose-params-from-post-body-after-read-it-once?noredirect=1&lq=1

        log.info(printRequest(multiReadRequest));

        /*
        log.info(
                "SampleFilter Logging Request  {} : {}", req.getMethod(),
                req.getRequestURI());
        */

        // invoke the servlet, or any other filters mapped to the target servlet
        //filterChain.doFilter(req,res);
        filterChain.doFilter(multiReadRequest, new HttpServletResponseWrapper((HttpServletResponse)servletResponse) {
            @Override
            public ServletOutputStream getOutputStream() throws IOException {
                return new DelegatingServletOutputStream(new TeeOutputStream(super.getOutputStream(), ps)
                );
            }
            @Override
            public PrintWriter getWriter() throws IOException {
                return new PrintWriter(new DelegatingServletOutputStream (new TeeOutputStream(super.getOutputStream(), ps))
                );
            }
        });
        // do stuff after the servlet finishes


        //how to log response body, not recommended
        // https://stackoverflow.com/questions/3242236/capture-and-log-the-response-body
        // first solution not work

        log.info("Log response body");
        log.info(baos.toString());
    }


    public String  printRequest(HttpServletRequest httpRequest) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nHTTP Method = "+httpRequest.getMethod()+"\n");

        sb.append("\nRequest URI = "+httpRequest.getRequestURI()+"\n");

        sb.append("\nHeaders\n");

        Enumeration headerNames = httpRequest.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            sb.append(headerName + " = " + httpRequest.getHeader(headerName));
            sb.append("\n");
        }

        sb.append("\nParameters\n");

        Enumeration params = httpRequest.getParameterNames();
        while(params.hasMoreElements()){
            String paramName = (String)params.nextElement();
            sb.append(paramName + " = " + httpRequest.getParameter(paramName));
            sb.append("\n");
        }
        sb.append("\n");

        sb.append("\nBody\n");
        sb.append(extractPostRequestBody(httpRequest));

        return sb.toString();
    }
    //get request body
    public String extractPostRequestBody(HttpServletRequest request) {
        Scanner s = null;
        try {
            s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s.hasNext() ? s.next() : "";

    }
    @Override
    public void destroy() {

    }
}
