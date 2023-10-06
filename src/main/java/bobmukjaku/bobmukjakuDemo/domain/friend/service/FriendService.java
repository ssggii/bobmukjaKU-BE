package bobmukjaku.bobmukjakuDemo.domain.friend.service;

import bobmukjaku.bobmukjakuDemo.domain.friend.Friend;
import bobmukjaku.bobmukjakuDemo.domain.friend.dto.BlockInfoDto;
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
    private final FriendRepository friendRepository;

    // 친구 등록
    public void createFriend(FriendUpdateDto friendUpdateDto) throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Friend friend = Friend.builder().member(member).friendUid(friendUpdateDto.friendUid()).isBlock(false).build();
        member.addFriend(friend);
    }

    // 친구 해제
    public void deleteFriend(FriendUpdateDto friendUpdateDto) throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Optional<Friend> friendToRemove = member.getFriendList().stream()
                .filter(friend -> friend.getFriendUid().equals(friendUpdateDto.friendUid()))
                .findFirst();
        if (friendToRemove.isPresent()) {
            member.deleteFriend(friendToRemove.get());
        } else {
            throw new Exception("Friend not found with ID: " + friendUpdateDto.friendUid());
        }
    }

    // 내 친구 목록 조회
    public List<FriendInfoDto> getMyFriends() throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        List<Member> friendMembers = member.getFriendList().stream()
                .filter(friend -> friend.getIsBlock().equals(false))
                .map(friend -> memberRepository.findById(friend.getFriendUid())
                        .orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)))
                .toList();

        if(!friendMembers.isEmpty()){
            return friendMembers.stream()
                    .map(FriendInfoDto::toDto)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    // 차단 등록
    public void createBlock(FriendUpdateDto friendUpdateDto) throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Friend block = Friend.builder().member(member).friendUid(friendUpdateDto.friendUid()).isBlock(true).build();
        member.addFriend(block);
    }

    // 차단 해제
    public void deleteBlock(FriendUpdateDto friendUpdateDto) throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        Optional<Friend> blockToRemove = member.getFriendList().stream()
                .filter(friend -> friend.getFriendUid().equals(friendUpdateDto.friendUid())).findFirst();
        if(blockToRemove.isPresent()){
            Friend block = blockToRemove.get();
            member.deleteFriend(block);
        } else {
            throw new Exception("Blocked friend not found with ID: " + friendUpdateDto.friendUid());
        }
    }

    // 내 차단 목록 조회
    public List<BlockInfoDto> getMyBlocks() throws Exception {
        Member member = memberRepository.findByMemberEmail(SecurityUtil.getLoginUsername()).orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
        List<Member> blockMembers = member.getFriendList().stream()
                .filter(friend -> friend.getIsBlock().equals(true))
                .map(friend -> memberRepository.findById(friend.getFriendUid())
                        .orElseThrow(()->new MemberException(MemberExceptionType.NOT_FOUND_MEMBER)))
                .toList();

        if(!blockMembers.isEmpty()){
            return blockMembers.stream()
                    .map(BlockInfoDto::toDto)
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
