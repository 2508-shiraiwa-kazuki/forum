package com.example.forum.service;

import com.example.forum.controller.form.ReportForm;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Serviceの宣言（これがないとControllerと関連づかない）
@Service
public class ReportService {
    // @Repositoryとの関連付け
    @Autowired
    ReportRepository reportRepository;

    /*
     * レコード全件取得処理
     */
    public List<ReportForm> findAllReport() {
        List<Report> results = reportRepository.findAll();
        // このあとViewに返すため、Entity → Formに入れなおす
        List<ReportForm> reports = setReportForm(results);
        return reports;
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
            result.setCreatedDate(result.getCreatedDate());
            result.setUpdatedDate(result.getUpdatedDate());
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
