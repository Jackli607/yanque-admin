package cn.edu.commons.apires;

import cn.edu.commons.apires.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private T data;

    // 定义一些静态方法 方便我们直接调用返回响应
    // 这边的状态码 可以在IErrorCode中定义也可以在这边写死
    // 通常在实际开发中 都是在接口中进行定义
    // 如果是在接口中定义的 就要保证所有的服务 都必须要能够统一成功和失败的返回值
    // 同时还要求前端必须要和后台的状态码保持一直 也就是我们在定义接口文档的时候就要说明
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    //成功, 有数据.
    public static <T> ApiResponse<T> success(T data, IErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(),
                errorCode.getMessage(), data);
    }

    // 成功, 没数据.
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(CommonErrorCode.SUCCESS.getCode(),
                CommonErrorCode.SUCCESS.getMessage(), null);
    }

    //失败,指定错误码.
    public static <T> ApiResponse<T> fail(IErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(),
                errorCode.getMessage(), null);
    }
    //失败, 指定错误码
    public static <T> ApiResponse<T> fail(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

}