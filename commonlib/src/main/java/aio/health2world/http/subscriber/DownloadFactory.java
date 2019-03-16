package aio.health2world.http.subscriber;


/**
 * Created by Administrator on 2018/6/8 0008.
 */

public class DownloadFactory {
    private String mUrl;
    private long mFileLength;
    private String mLastModify;
    private DownloadHelper mDownloadHelper;

    DownloadFactory(DownloadHelper downloadHelper) {
        this.mDownloadHelper = downloadHelper;
    }

    DownloadFactory url(String url) {
        this.mUrl = url;
        return this;
    }

    DownloadFactory fileLength(long fileLength) {
        this.mFileLength = fileLength;
        return this;
    }

    DownloadFactory lastModify(String lastModify) {
        this.mLastModify = lastModify;
        return this;
    }

    DownloadType buildNormalDownload() {
        DownloadType type = new DownloadType.NormalDownload();
        type.mUrl = this.mUrl;
        type.mFileLength = this.mFileLength;
        type.mLastModify = this.mLastModify;
        type.mDownloadHelper = this.mDownloadHelper;
        return type;
    }

    DownloadType buildContinueDownload() {
        DownloadType type = new DownloadType.ContinueDownload();
        type.mUrl = this.mUrl;
        type.mFileLength = this.mFileLength;
        type.mLastModify = this.mLastModify;
        type.mDownloadHelper = this.mDownloadHelper;
        return type;
    }

    DownloadType buildMultiDownload() {
        DownloadType type = new DownloadType.MultiThreadDownload();
        type.mUrl = this.mUrl;
        type.mFileLength = this.mFileLength;
        type.mLastModify = this.mLastModify;
        type.mDownloadHelper = this.mDownloadHelper;
        return type;
    }

    DownloadType buildAlreadyDownload() {
        DownloadType type = new DownloadType.AlreadyDownloaded();
        type.mUrl = this.mUrl;
        type.mFileLength = this.mFileLength;
        type.mLastModify = this.mLastModify;
        type.mDownloadHelper = this.mDownloadHelper;
        return type;
    }
}
