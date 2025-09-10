package com.example.forum.controller;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.controller.form.ReportForm;
import com.example.forum.service.CommentService;
import com.example.forum.service.ReportService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

//Controllerの宣言
@Controller
public class ForumController {

    //@Serviceへの関連付け
    @Autowired
    ReportService reportService;
    @Autowired
    CommentService commentService;

    /*
     * 投稿内容表示処理
     */
    @GetMapping
    public ModelAndView top(HttpServletRequest request){
        ModelAndView mav = new ModelAndView();

        // getParameterを使用したいのでメソッドの引数にHttpServletRequestを設定する。簡易Twitterより。
        String startDate = request.getParameter("start");
        String endDate = request.getParameter("end");
        // 投稿を日付で絞り込み全件取得
        List<ReportForm> contentData = reportService.findAllReport(startDate, endDate);
        // コメントを全件取得
        List<CommentForm> commentData = commentService.findAllComment();
        // コメント格納用の空Formを用意
        CommentForm commentForm = new CommentForm();
        // 画面遷移先を指定
        mav.setViewName("/top");
        // 投稿データオブジェクトを補完
        mav.addObject("contents", contentData);
        mav.addObject("comments", commentData);
        mav.addObject("commentForm", commentForm);
        mav.addObject("start", startDate);
        mav.addObject("end", endDate);
        return mav;
    }

    /*
     * 新規投稿画面表示(入力フォームを表示するだけなのでServiceへつなげる必要はない)
     */
    @GetMapping("/new")
    public ModelAndView newContent() {
        ModelAndView mav = new ModelAndView();
        // formを準備
        ReportForm reportForm = new ReportForm();
        // 画面遷移先を指定
        mav.setViewName("/new");
        // 準備した空のFormを保管
        mav.addObject("formModel", reportForm);
        return mav;
    }

    /*
     * 新規投稿処理
     */
    @PostMapping("/add")
    public ModelAndView addContent(@ModelAttribute("formModel") @Validated ReportForm reportForm, BindingResult result){
        // バリデーションでエラーが発生した場合の処理
        // 本来は例外がスローされ500エラーなどが表示されるが、BindingResultを引数にすることでここにエラー情報を格納し、
        // Formで設定したバリデーションのアノテーションプロパティを表示することができる。
        if(result.hasErrors()){
            // 「/new」に戻りエラーメッセージを表示する。
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/new");
            return mav;
        }
        // 投稿をテーブルに格納
        reportService.saveReport(reportForm);
        // rootへリダイレクト
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿削除処理
     */
    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteContent(@PathVariable Integer id){
        reportService.deleteReport(id);
        return new ModelAndView("redirect:/");
    }

    /*
     * 投稿の編集画面表示
     */
    // htmlから持ってきたcontent.idををserviceに渡したい
    @GetMapping("/edit/{id}")
    public ModelAndView editContent(@PathVariable Integer id) {
        ModelAndView mav = new ModelAndView();
        // 投稿を取得(@Entityに指定しているreportを変数とする)
        ReportForm report = reportService.editReport(id);
        // ModelAndViewに保管(htmlの入力がformだからformModel)
        mav.addObject("formModel", report);
        // 画面遷移先を指定
        mav.setViewName("/edit");
        return mav;
    }

    /*
     * 投稿編集処理
     */
    @PutMapping("/update/{id}")
    // パス情報からid、入力内容からreport(編集後のテキスト)を取得
    public ModelAndView updateContent(@PathVariable Integer id,
                                      @ModelAttribute("formModel") @Validated ReportForm report,
                                      BindingResult result){
        // バリデーション
        if(result.hasErrors()){
            ModelAndView mav = new ModelAndView();
            mav.setViewName("/edit");
            return mav;
        }

        // idは現状nullでこのままだとsaveメソッドで「新規登録」になってしまうので元のidをセットしておく
        report.setId(id);
        report.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        // 更新なのでsaveとする
        // saveReportメソッドは新規投稿追加ですでに実装済みのため、Serviceで新たに作成する必要はない
        reportService.saveReport(report);
        // 更新後トップ画面へ遷移
        return new ModelAndView("redirect:/");
    }

    /*
     * コメントの登録
     * やりたいこと：Serviceに「contentId」と「comment」を渡し、DB「comments」に登録する。
     */
    @PostMapping("/comment/{contentId}")
    //@PathVariableでcontentIdを、@ModelAttribute("commentForm")でHTMLからcommentをそれぞれ取得
    public ModelAndView insertComment(@PathVariable Integer contentId,
                                      @ModelAttribute("commentForm") @Validated CommentForm commentForm,
                                      BindingResult result){
        if(result.hasErrors()){
            return new ModelAndView("redirect:/");
        }
        //Form「commentForm」にcontentIdを格納
        commentForm.setContentId(contentId);
        //Serviceへ渡す
        commentService.saveComment(commentForm);
        //ForumControllerのtopメソッドを呼び出し
        return new ModelAndView("redirect:/");
    }

    /*
     * コメントの編集画面表示
     */
    @GetMapping("/commentEdit/{commentId}")
    public ModelAndView commentEdit(@PathVariable Integer commentId){
        ModelAndView mav = new ModelAndView();
        CommentForm comment = commentService.commentEdit(commentId);
        mav.addObject("commentEdit", comment);
        mav.setViewName("/commentEdit");
        return mav;
    }

    /*
     * コメントの更新
     */
    @PutMapping("/commentEdit/{commentId}")
    public ModelAndView updateComment(@PathVariable Integer commentId, @ModelAttribute("commentEdit") CommentForm comment){
        comment.setId(commentId);
        commentService.saveComment(comment);
        return new ModelAndView("redirect:/");
    }

    /*
     * コメントの削除
     */
    @DeleteMapping("/commentDelete/{commentId}")
    public ModelAndView commentDelete(@PathVariable Integer commentId){
        commentService.deleteComment(commentId);
        return new ModelAndView("redirect:/");
    }
}
