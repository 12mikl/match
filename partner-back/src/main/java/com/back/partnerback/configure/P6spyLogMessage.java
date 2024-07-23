package com.back.partnerback.configure;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * @author yang
 * @create 2024-05-09 21:45
 */
public class P6spyLogMessage implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared,String sql, String url) {
        return StringUtils.isNotBlank(sql) ? DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss")
                + " | 耗时 " + elapsed + " ms | SQL 语句：" + StringUtils.LF + sql.replaceAll("[\\s]+", StringUtils.SPACE) + ";" : StringUtils.EMPTY;
    }
}
