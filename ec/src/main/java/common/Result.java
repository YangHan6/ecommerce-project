package common;

import lombok.Data;

@Data
public class Result<T> {
    // 业务状态码（200 成功，500服务器失效）
    private Integer code;
    // 响应提示信息
    private String message;
    // 核心响应数据（第一层 data，对应前端 res.data）
    private T data;

    // 成功响应静态方法（有参数）
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        return result;
    }

    // 失败响应静态方法（简化调用）
    public static <T> Result<T> error(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
}
