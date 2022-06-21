package com.atguigu.gmall.product.exception;


import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     *
     * 业务异常
     * @param e
     * @return
     */
    @ExceptionHandler(GmallException.class)
    public Result handleBizException(GmallException e){

        Result<String> result = new Result<>();
        result.setCode(e.getCode());
        result.setMessage(e.getMessage());
        result.setData("");
        return result;
    }

    /**
     *
     *  处理其他异常
     * @param e
     * @return
     */

    @ExceptionHandler(Exception.class)
    public Result handleOtherException(Exception e){
        Result<Object> fail = Result.fail();
        fail.setMessage(e.getMessage());
        return fail;

    }



}
