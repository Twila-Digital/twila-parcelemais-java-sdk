package twila.parcelemais;

import java.util.List;
import lombok.Value;

@Value
public class PagedResult<T> {
    List<T> items;
    boolean hasNext;
    boolean hasPrevious;
    int pageNumber;
    int pageSize;
    int totalCount;
}
