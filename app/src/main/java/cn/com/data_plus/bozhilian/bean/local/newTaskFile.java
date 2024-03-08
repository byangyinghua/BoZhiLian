package cn.com.data_plus.bozhilian.bean.local;

import com.ly723.db.interfaces.Column;
import com.ly723.db.interfaces.Table;

import cn.com.data_plus.bozhilian.App;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.util.TimeUtil;

@Table
public class newTaskFile {
    @Column(isPrimaryKey = true)
    private String fileId;
    @Column
    private String fileName;
    @Column
    private String fileLength;
    @Column
    private int playPosition;
    @Column
    private int FileType;

    @Override
    public String toString() {
        return "Task" + '\n' +
                "--------------------------------------" + '\n' +
                "id=" + fileId + '\n' +
                "fileName=" + fileName + '\n' +
                "fileLength=" + fileLength + '\n' +
                "playPosition=" + playPosition + '\n' +
                "FileType=" + FileType + '\n' +
                "--------------------------------------";
    }


}
