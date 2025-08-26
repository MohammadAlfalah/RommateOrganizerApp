package com.example.roommateorganizer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roommateorganizer.model.ChatMsg
import com.example.roommateorganizer.model.MessageDoc
import com.example.roommateorganizer.model.toChatMsg
import com.example.roommateorganizer.util.ChatAdapter
import com.google.firebase.firestore.ListenerRegistration

class ChatFragment : Fragment() {

    private lateinit var list: RecyclerView
    private lateinit var input: EditText
    private lateinit var send: Button
    private lateinit var adapter: ChatAdapter

    private var sub: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_chat, container, false)
        list = root.findViewById(R.id.chat_list)
        input = root.findViewById(R.id.chat_input)
        send = root.findViewById(R.id.chat_send)

        adapter = ChatAdapter()
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }

        send.setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            val me = F.meUid ?: return@setOnClickListener
            val hh = F.householdId ?: return@setOnClickListener

            val doc = MessageDoc(
                uid = me,
                text = text,
                createdAt = F.now()
            )

            F.db.collection("households").document(hh)
                .collection("messages").add(doc)
            input.setText("")
        }

        return root
    }

    override fun onStart() {
        super.onStart()

        val hh = F.householdId ?: return
        sub = F.db.collection("households").document(hh)
            .collection("messages")
            .orderBy("createdAt")
            .addSnapshotListener { qs, err ->
                if (err != null || qs == null) return@addSnapshotListener

                val me = F.meUid
                val chatMsgs: List<ChatMsg> = qs.documents.mapNotNull { snap ->
                    snap.toObject(MessageDoc::class.java)
                        ?.copy(id = snap.id)
                        ?.toChatMsg(me)
                }

                adapter.submitList(chatMsgs)
                if (chatMsgs.isNotEmpty()) {
                    list.scrollToPosition(chatMsgs.lastIndex)
                }
            }
    }




    override fun onStop() {
        sub?.remove()
        sub = null
        super.onStop()
    }
}
