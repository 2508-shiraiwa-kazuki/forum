package com.example.forum.service;

import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Report;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Serviceの宣言（これがないとControllerと関連づかない）
@Service
public class ReportService {
    // @Repositoryとの関連付け
    @Autowired
    ReportRepository reportRepository;

    /*
     * レコード全件取得処理 + 日付絞り込み
     */
    public List<ReportForm> findAllReport(String startDate, String endDate) {
        // 現在時刻の取得と形式の指定
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 開始日の設定
        String startTime;
        if(!StringUtils.isBlank(startDate)){
            startTime = startDate + " 00:00:00";
        } else {
            startTime = "2020-01-01 00:00:00";
        }
        // 参照するDBのカラム型に合わせるためTimeStamp型に変換
        Timestamp start = Timestamp.valueOf(startTime);

        // 終了日の設定
        String endTime;
        if(!StringUtils.isBlank(endDate)){
            endTime = endDate + " 23:59:59";
        } else {
            // 現在時刻に設定
            endTime = sdf.format(now);;
        }
        Timestamp end = Timestamp.valueOf(endTime);

        // ReportRepositoryへfindByCreatedDateBetweenで渡す
        List<Report> results = reportRepository.findByCreatedDateBetweenOrderByUpdatedDateDesc(start, end);
        // このあとViewに返すため、Entity → Formに入れなおす
        return setReportForm(results);
    }
    /*
     * DBから取得したデータをFormに設定
     */
    private List<ReportForm> setReportForm(List<Report> results) {
        List<ReportForm> reports = new ArrayList<>();

        for (int i = 0; i < results.size(); i++) {
            ReportForm report = new ReportForm();
            Report result = results.get(i);
            report.setId(result.getId());
            report.setContent(result.getContent());
            report.setCreatedDate(result.getCreatedDate());
            report.setUpdatedDate(result.getUpdatedDate());
            reports.add(report);
        }
        return reports;
    }

    /*
     * レコード追加
     */
    public void saveReport(ReportForm reqReport) {
        // form → Entityに入れなおす
        Report saveReport = setReportEntity(reqReport);
        reportRepository.save(saveReport);
    }

    /*
     * リクエストから取得した情報をEntityに設定
     */
    private Report setReportEntity(ReportForm reqReport) {
        Report report = new Report();
        report.setId(reqReport.getId());
        report.setContent(reqReport.getContent());
        report.setCreatedDate(reqReport.getCreatedDate());
        report.setUpdatedDate(reqReport.getUpdatedDate());
        return report;
    }

    /*
     * 投稿削除
     */
    public void deleteReport(Integer id) {
        reportRepository.deleteById(id);
    }

    /*
     * 条件に該当するレコードの取得処理
     */
    public ReportForm editReport(Integer id){
        //setReportForm(List<Report> results)なのでList型で定義する
        List<Report> results = new ArrayList<>();
        // orElse(null)は、idが見つからなかったときにnullを返すもの。nullの場合Optional型が返される。
        // orElse(null)がないと「nullの場合どうするの？」という予期せぬエラーが発生するのでコンパイルエラーになる。
        results.add(reportRepository.findById(id).orElse(null));
        //resultsをformに移し替える
        List<ReportForm> reports = setReportForm(results);
        //Listの中から最初の要素のみ返す
        return reports.get(0);
    }
}
