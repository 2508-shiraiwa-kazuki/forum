package com.example.forum.service;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.ReportRepository;
import com.example.forum.repository.entity.Comment;
import com.example.forum.repository.entity.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ReportRepository reportRepository;

    /*
     * コメントの表示
     * やりたいこと：①Controllerから渡されたcontentIdとDB「Comments.content_id」が一致するレコードを取得
     * 　　　　　　：②EntityとしてDBから受け取った結果をFormに変換してControllerへ返す
     */
    public List<CommentForm> findAllComment(){
        // コメントを全件取得し、リストに格納
        List<Comment> results = commentRepository.findAllByOrderByUpdatedDateDesc();
        // 取得した結果をEntityからFormに変換してControllerに返す
        return setCommentForm(results);
    }
    private List<CommentForm> setCommentForm(List<Comment> results){
        // FormのCommentForm型のListを用意する
        List<CommentForm> comments =new ArrayList<>();
        // resultsに格納したデータの分だけ以降の処理を繰り返し実行
        for(int i = 0; i < results.size(); i++){
            CommentForm comment = new CommentForm();
            // 1レコードずつ取り出しCommentForm型の変数commentに格納していく
            Comment result = results.get(i);
            comment.setId(result.getId());
            comment.setContentId(result.getContentId());
            comment.setComment(result.getComment());
            // 1レコード分をListに格納
            comments.add(comment);
        }
        // レコードの集合体(List)を返す
        return comments;
    }
    /*
     * コメントの登録
     * やりたいこと：Controllerから渡されたcommentFormをEntityに変換してCommentRepositoryへ渡す
     */
    public void saveComment(CommentForm commentForm){
        // コメントの登録
        commentRepository.save(setCommentEntity(commentForm));
        // コメントの登録に合わせて元の投稿の更新日時を更新するため、元の投稿データを取得
        Report updateReport = reportRepository.findById(commentForm.getContentId()).orElse(null);
        // 取得した投稿データの更新日時にメソッド実行時の現在時刻をセット
        updateReport.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        // saveメソッドを実行
        reportRepository.save(updateReport);
    }

    private Comment setCommentEntity(CommentForm commentForm){
        Comment comment = new Comment();
        comment.setId(commentForm.getId());
        comment.setContentId(commentForm.getContentId());
        comment.setComment(commentForm.getComment());
        return comment;
    }

    /*
     * コメントの編集画面表示
     */
    public CommentForm commentEdit(Integer commentId){
        List<Comment> results = new ArrayList<>();
        results.add(commentRepository.findById(commentId).orElse(null));
        List<CommentForm> comment = setCommentForm(results);
        return comment.get(0);
    }

    /*
     * コメントの削除
     */
    public void deleteComment(Integer commentId){
        commentRepository.deleteById(commentId);
    }
}
