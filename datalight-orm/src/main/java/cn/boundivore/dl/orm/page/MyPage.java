package cn.boundivore.dl.orm.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Description: 数据库分页封装
 * Created by: Boundivore
 * E-mail: boundivore@foxmail.com
 * Creation time: 2023/5/28
 * Modification description:
 * Modified by:
 * Modification time:
 * Version: V1.0
 */
public class MyPage<T> implements IPage<T> {

    private static final long serialVersionUID = 1L;

    protected List<T> records = Collections.emptyList();


    protected long total = 0;

    protected long size = 10;


    protected long current = 1;

    @Setter
    protected List<OrderItem> orders = new ArrayList<>();


    protected boolean optimizeCountSql = false;

    protected boolean searchCount = true;

    @Setter
    protected boolean optimizeJoinOfCountSql = true;

    @Setter
    protected String countId;

    @Setter
    protected Long maxLimit;

    public MyPage() {
    }


    public MyPage(Long current, Long size) {
        this(current, size, 0L);
    }

    public MyPage(Long current, Long size, Long total) {
        this(current, size, total, true);
    }

    public MyPage(Long current, Long size, boolean searchCount) {
        this(current, size, 0L, searchCount);
    }

    public MyPage(Long current, Long size, Long total, boolean searchCount) {
        if (current != null && current >= 1) {
            this.current = current;
        }

        if (size != null && size >= 1) {
            this.size = size;
        }

        if (total != null && total >= 0) {
            this.total = total;
        }
        this.searchCount = searchCount;
    }


    public boolean hasPrevious() {
        return this.current > 1;
    }

    public boolean hasNext() {
        return this.current < this.getPages();
    }

    @Override
    public List<T> getRecords() {
        return this.records;
    }

    @Override
    public MyPage<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    @Override
    public long getTotal() {
        return this.total;
    }

    @Override
    public MyPage<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public MyPage<T> setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public long getCurrent() {
        return this.current;
    }

    @Override
    public MyPage<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    @Override
    public String countId() {
        return this.countId;
    }

    @Override
    public Long maxLimit() {
        return this.maxLimit;
    }

    private String[] mapOrderToArray(Predicate<OrderItem> filter) {
        List<String> columns = new ArrayList<>(orders.size());
        orders.forEach(i -> {
            if (filter.test(i)) {
                columns.add(i.getColumn());
            }
        });
        return columns.toArray(new String[0]);
    }

    private void removeOrder(Predicate<OrderItem> filter) {
        for (int i = orders.size() - 1; i >= 0; i--) {
            if (filter.test(orders.get(i))) {
                orders.remove(i);
            }
        }
    }


    public MyPage<T> addOrder(OrderItem... items) {
        orders.addAll(Arrays.asList(items));
        return this;
    }

    public MyPage<T> addOrder(List<OrderItem> items) {
        orders.addAll(items);
        return this;
    }

    @Override
    public List<OrderItem> orders() {
        return this.orders;
    }

    @Override
    public boolean optimizeCountSql() {
        return optimizeCountSql;
    }

    public static <T> MyPage<T> of(long current, long size, long total, boolean searchCount) {
        return new MyPage<>(current, size, total, searchCount);
    }

    @Override
    public boolean optimizeJoinOfCountSql() {
        return optimizeJoinOfCountSql;
    }

    public MyPage<T> setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
        return this;
    }

    public MyPage<T> setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
        return this;
    }

    @Override
    public long getPages() {
        // github issues/3208
        return IPage.super.getPages();
    }

    public static <T> MyPage<T> of(Long current, Long size) {
        return of(current, size, 0L);
    }

    public static <T> MyPage<T> of(Long current, Long size, Long total) {
        return of(current, size, total, true);
    }

    public static <T> MyPage<T> of(Long current, Long size, boolean searchCount) {
        return of(current, size, 0, searchCount);
    }

    @Override
    public boolean searchCount() {
        if (total < 0) {
            return false;
        }
        return searchCount;
    }

    @Deprecated
    public String getCountId() {
        return this.countId;
    }

    @Deprecated
    public Long getMaxLimit() {
        return this.maxLimit;
    }

    @Deprecated
    public List<OrderItem> getOrders() {
        return this.orders;
    }

    @Deprecated
    public boolean isOptimizeCountSql() {
        return this.optimizeCountSql;
    }

    @Deprecated
    public boolean isSearchCount() {
        return this.searchCount;
    }

}
