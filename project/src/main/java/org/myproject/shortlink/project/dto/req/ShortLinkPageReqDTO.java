package org.myproject.shortlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.myproject.shortlink.project.dao.entity.ShortLinkDO;

@Data
public class ShortLinkPageReqDTO extends Page<ShortLinkDO>{
    private String gid;
}
