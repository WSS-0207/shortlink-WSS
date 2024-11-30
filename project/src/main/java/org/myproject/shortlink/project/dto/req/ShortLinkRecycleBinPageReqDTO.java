package org.myproject.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.myproject.shortlink.project.dao.entity.ShortLinkDO;

import java.util.List;

@Data
public class ShortLinkRecycleBinPageReqDTO extends Page<ShortLinkDO> {
    private List<String> gidList;
}
