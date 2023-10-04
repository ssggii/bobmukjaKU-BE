package bobmukjaku.bobmukjakuDemo.domain.friend.service;

import bobmukjaku.bobmukjakuDemo.domain.friend.Friend;
import bobmukjaku.bobmukjakuDemo.domain.friend.dto.FriendInfoDto;
import bobmukjaku.bobmukjakuDemo.domain.friend.dto.FriendUpdateDto;
import bobmukjaku.bobmukjakuDemo.domain.friend.repository.FriendRepository;
import bobmukjaku.bobmukjakuDemo.domain.member.Member;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberException;
import bobmukjaku.bobmukjakuDemo.domain.member.exception.MemberExceptionType;
import bobmukjaku.bobmukjakuDemo.domain.member.repository.MemberRepository;
import bobmukjaku.bobmukjakuDemo.global.utility.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final MemberRepository memberRepository;

    // 친구 등록
    public void createFriend(FriendUpdateDto friendUpdateDto) throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Friend friend = Friend.builder().member(member).friendId(friendUpdateDto.friendId()).build();
        friend.setIsBlock(false);
        member.addFriend(friend);
    }

    // 친구 해제
    public void deleteFriend(FriendUpdateDto friendUpdateDto) throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Optional<Friend> friendToRemove = member.getFriendList().stream()
                .filter(friend -> friend.getId().equals(friendUpdateDto.friendId()))
                .findFirst();
        if (friendToRemove.isPresent()) {
            Friend friend = friendToRemove.get();
            member.deleteFriend(friend);
        } else {
            throw new Exception("Friend not found with ID: " + friendUpdateDto.friendId());
        }
    }

    // 내 친구 목록 조회
    public List<Long> getMyFriends() throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        List<Long> friendIdList = member.getFriendList().stream()
                .filter(friend -> friend.getIsBlock().equals(false))
                .map(friend -> friend.getFriendId()).collect(Collectors.toList());

        if(!friendIdList.isEmpty()){
            return friendIdList;
        } else {
            return null;
        }
    }

    // 차단 등록
    public void createBlock(FriendUpdateDto friendUpdateDto) throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Friend block = Friend.builder().member(member).friendId(friendUpdateDto.friendId()).build();
        block.setIsBlock(true);
        member.addFriend(block);
    }

    // 차단 해제
    public void deleteBlock(FriendUpdateDto friendUpdateDto) throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Optional<Friend> blockToRemove = member.getFriendList().stream()
                .filter(friend -> friend.getFriendId().equals(friendUpdateDto.friendId())).findFirst();
        if(blockToRemove.isPresent()){
            Friend block = blockToRemove.get();
            member.deleteFriend(block);
        } else {
            throw new Exception("Blocked friend not found with ID: " + friendUpdateDto.friendId());
        }
    }

    // 내 차단 목록 조회
    public List<Long> getMyBlocks() throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        List<Long> blockIdList = member.getFriendList().stream()
                .filter(block -> block.getIsBlock().equals(true))
                .map(block -> block.getFriendId()).collect(Collectors.toList());

        if(!blockIdList.isEmpty()){
            return blockIdList;
        } else {
            return null;
        }
    }
}
