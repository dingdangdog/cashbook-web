package top.oldmoon.cashbook.cloud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.oldmoon.cashbook.cloud.dao.OnlineDao;
import top.oldmoon.cashbook.cloud.model.*;
import top.oldmoon.cashbook.cloud.util.FileUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 在线服务
 *
 * @author dingdangdog
 * @since 1.0
 */
@Service
public class OnlineServiceImpl implements OnlineService {
    @Autowired
    OnlineDao onlineDao;

    @Value("${filePath}")
    private String filePath;

    @Override
    public AuthInfo getAuth(String key) {
        return onlineDao.getAuth(key);
    }

    @Override
    public void upload(Auth auth, Data data) throws IOException {
        LocalDate day = LocalDate.now();
        LocalTime time = LocalTime.now();
        String fileName = filePath + "/" + day + "/" + data.getKey() + time.toString().replace(":", "") + ".json";
        FileUtils.writeFile(fileName, data.getJson());

        LogUpload upload = new LogUpload(null, data.getKey(), day.toString(), time.toString(), fileName);
        onlineDao.saveUploadLog(upload);
    }

    @Override
    public Data download(String key) throws IOException {
        AuthInfo auth = getAuth(key);
        Data data = new Data(key, null);
        if (auth == null) {
            return data;
        }

        LogUpload upload = onlineDao.getLastUploadLog(key);
        if (upload == null || upload.getId() == null) {
            data.setJson("");
            return data;
        }
        String json = FileUtils.readFile(upload.getFilePath());
        data.setJson(json);

        LogDownload logDownload = new LogDownload(null, key, LocalDate.now().toString(), LocalTime.now().toString(), upload.getFilePath());
        onlineDao.saveDownloadLog(logDownload);
        return data;
    }

    @Override
    public boolean generateAuth(Integer sum) {
        List<String> keys = new ArrayList<>();
        for(int i = 0; i < sum; i ++) {
            String key = UUID.randomUUID().toString().replace("-", "");
            keys.add(key);
        }
        onlineDao.generateAuth(keys);
        return true;
    }
}
