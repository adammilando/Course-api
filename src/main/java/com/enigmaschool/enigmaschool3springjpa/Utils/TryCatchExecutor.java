package com.enigmaschool.enigmaschool3springjpa.Utils;

import com.enigmaschool.enigmaschool3springjpa.Exception.MaxDataException;
import com.enigmaschool.enigmaschool3springjpa.Exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Supplier;

public class TryCatchExecutor {
    public static<T> T execute(Supplier<T> supplier, String notFoundMaessage, String MaxDatamessage){
        try {
            T result = supplier.get();
            if (result == null){
                throw new NotFoundException(notFoundMaessage);
            }
            if (result instanceof List && ((List) result).isEmpty()){
                throw new NotFoundException(notFoundMaessage);
            }
            if (result instanceof Page && ((Page) result).isEmpty()){
                throw new NotFoundException(notFoundMaessage);
            }
            return result;
        }catch (MaxDataException | NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
