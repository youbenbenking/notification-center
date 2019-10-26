package com.notification.common.result;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import com.notification.common.constant.ClientConstants;
import com.notification.common.exception.NotSerializableException;

public class PaginationDataResult<T> implements Serializable {
    private static final long serialVersionUID = -121894632229176784L;
    private List<T> data;
    private boolean success;
    private String errorCode;
    private String message;

    /**
     * 当前页索引
     */
    private int currentPage;
    /**
     * 每页条数
     */
    private int pageSize = ClientConstants.DEFAULT_PAGE_SIZE;
    /**
     * 总条数
     */
    private long totalCount;
    /**
     * 总页数
     */
    private int totalPage;

    public PaginationDataResult() {
    }

    public static <T> PaginationDataResult<T> from(final List<T> data, final PaginationDataResult result) {
        return PaginationDataResult.success(data)
            .page(result.getCurrentPage(), result.getPageSize(), result.getTotalPage(), result.getTotalCount());
    }

    public static <T> PaginationDataResult<T> empty(final int currentPage, final int pageSize) {
        return PaginationDataResult.<T>success()
            .page(currentPage, currentPage, 0, 0L);
    }

    public static <T> PaginationDataResult<T> success(List<T> data) {
        if (Objects.isNull(data)) {
            return PaginationDataResult.success();
        }
        if (!(data instanceof Serializable)) {
            throw new NotSerializableException(data.getClass());
        }

        PaginationDataResult<T> dataResult = new PaginationDataResult<>();
        dataResult.data = data;
        dataResult.success = true;
        dataResult.message = "ok";
        return dataResult;
    }

    public static <T> PaginationDataResult<T> success() {
        PaginationDataResult<T> dataResult = new PaginationDataResult<>();
        dataResult.data = null;
        dataResult.success = true;
        dataResult.message = "ok";
        return dataResult;
    }

    public static <T> PaginationDataResult<T> success(List<T> data, String message) {
        if (Objects.isNull(data)) {
            return PaginationDataResult.success();
        }
        if (!(data instanceof Serializable)) {
            throw new NotSerializableException(data.getClass());
        }

        PaginationDataResult<T> dataResult = new PaginationDataResult<>();
        dataResult.data = data;
        dataResult.success = true;
        dataResult.message = message;
        return dataResult;
    }

    public static <T> PaginationDataResult<T> fail(String errorCode, String errorMessage) {
        PaginationDataResult<T> dataResult = new PaginationDataResult<>();
        dataResult.errorCode = errorCode;
        dataResult.success = false;
        dataResult.message = errorMessage;
        return dataResult;
    }

    public PaginationDataResult<T> page(final int currentPage, final int pageSize, final int totalPage, final long totalCount) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalCount = totalCount;
        return this;
    }

    public static <T> PaginationDataResult<T> fail(String errorCode, String errorMessage, Object... args) {
        return fail(errorCode, MessageFormat.format(errorMessage, args));
    }

    public boolean isSuccAndNotNull() {
        return success && data != null;
    }

    public List<T> getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
