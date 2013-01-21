//
//  AppDelegate.h
//  Banden Lehia
//
//  Created by Iker Mendilibar on 22/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreData/CoreData.h>
#import <AVFoundation/AVFoundation.h>
#import <MediaPlayer/MediaPlayer.h>
#import "BLAPI.h"
#import "Track.h"
#import "GAI.h"

@class PlayerViewController;

@interface AppDelegate : UIResponder <UIApplicationDelegate, AVAudioSessionDelegate, UIAlertViewDelegate>

typedef enum {
    Play = 0,
    Stop = 1,
    FinishPlayingList = 2,
    NotInitialized = 3
} PlayerState;


@property(strong, nonatomic) UIWindow *window;


#pragma mark - CoreData properties
@property (readonly, strong, nonatomic) NSManagedObjectContext *managedObjectContext;
@property (readonly, strong, nonatomic) NSManagedObjectModel *managedObjectModel;
@property (readonly, strong, nonatomic) NSPersistentStoreCoordinator *persistentStoreCoordinator;

#pragma mark - AVPlayer properties
@property(strong, nonatomic) AVPlayer *player;
@property(strong, nonatomic) AVPlayerItem *playerItem;
@property(nonatomic) PlayerState playerState;

#pragma mark - Data properties
@property(strong, nonatomic) BLAPI *api;
@property(strong, atomic) NSArray *songsArray;
@property(strong, atomic) Track *currentTrack;
@property(nonatomic) int nextSongIndex;
@property(nonatomic) BOOL isLoadingTrack;
@property(nonatomic) Boolean playerInitialized1;

//Cuando no se se pulsa un botón para cambiar la canción pone la variable a TRUE para indicar que es cambio de canción forzado
@property(nonatomic) BOOL playNextSongTriggered;
@property(nonatomic) NSInteger triggredIndex;

#pragma mark - AlertView Methods
-(void)alerViewWithText:(NSString*)error;

#pragma mark - Player Methods
-(void)playNextsong;
-(void)playPreviusSong;
-(void)playSongAtIndex:(int)index;
-(PlayerState)playOrPause;
-(void)setupApplicationAudio:(UITabBarController*)vc;

#pragma mark - CoreData Methods
-(void)removeAllData;

#pragma mark - Other Methods
-(void)logout;

@end
