package com.example.forum.service;

import com.example.forum.controller.form.CommentForm;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    /*
     * コメントの表示
     * やりたいこと：①Controllerから渡されたcontentIdとDB「Comments.content_id」が一致するレコードを取得
     * 　　　　　　：②EntityとしてDBから受け取った結果をFormに変換してControllerへ返す
     */
    public List<CommentForm> findAllComment(){
        // コメントを全件取得し、リストに格納
        List<Comment> results = commentRepository.findAll();
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
        commentRepository.save(setCommentEntity(commentForm));
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
