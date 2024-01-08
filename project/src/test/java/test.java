import cn.hutool.core.util.StrUtil;

public class test {
    public static final String SQL = "CREATE TABLE `t_link_goto_%d` (\n" +
            "  `id` bigint AUTO_INCREMENT NOT NULL COMMENT 'ID',\n" +
            "  `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '完整短链接',\n" +
            "  `gid` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '分组标识',\n" +
            "  PRIMARY KEY (`id`),\n" +
            "  UNIQUE KEY `idx_fullShortUrl_gid` (`full_short_url`,`gid`) USING BTREE\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;\n";
    public static void main(String[] args) {
        for (Integer i = 0; i < 16; i++) {
            String replace = StrUtil.replace(SQL, "%d", i.toString());
            System.out.print(replace);
        }
    }
}
