sealed class FriendEvent {

    data class LoadFriends(
        val userId: Long
    ) : FriendEvent()

    data class LoadPendingRequests(
        val userId: Long
    ) : FriendEvent()

    data class SendFriendRequest(
        val senderId: Long,
        val receiverId: Long
    ) : FriendEvent()

    data class AcceptFriendRequest(
        val user1: Long,
        val user2: Long
    ) : FriendEvent()
    data class RemoveFriend(
        val user1: Long,
        val user2: Long
    ) : FriendEvent()

    data class SearchFriend(
        val query: String
    ) : FriendEvent()

    data class ConnectFriend(
        val friendId: Long
    ) : FriendEvent()
    data class SendChallenge(
        val friendId: Long,
        val matchType: String
    ) : FriendEvent()

    data object ClearMessage : FriendEvent()
}