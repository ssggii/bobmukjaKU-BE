package bobmukjaku.bobmukjakuDemo.domain.member.exception;

import bobmukjaku.bobmukjakuDemo.global.exception.BaseException;
import bobmukjaku.bobmukjakuDemo.global.exception.BaseExceptionType;

public class MemberException extends BaseException {

    private BaseExceptionType exceptionType;

    public MemberException(BaseExceptionType exceptionType){
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
