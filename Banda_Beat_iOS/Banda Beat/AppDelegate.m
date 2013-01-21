//
//  AppDelegate.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 22/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "AppDelegate.h"
#import "SDImageCache.h"
#import "PlayerViewController.h"
#import "GAI.h"

// Dispatch period in seconds
static const NSInteger kGANDispatchPeriodSec = 10;

@interface AppDelegate ()

@end


@implementation AppDelegate

@synthesize managedObjectContext = _managedObjectContext;
@synthesize managedObjectModel = _managedObjectModel;
@synthesize persistentStoreCoordinator = _persistentStoreCoordinator;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    _api = [BLAPI sharedInstance];
    
    
    [GAI sharedInstance].trackUncaughtExceptions = YES;
    [GAI sharedInstance].dispatchInterval = 20;
    [[GAI sharedInstance] trackerWithTrackingId:@"UA-36457067-1"];
    
    
    [[UITabBar appearance] setBackgroundImage:[UIImage imageNamed:@"tabbar_background"]];
    [[UITabBar appearance] setSelectionIndicatorImage:[UIImage imageNamed:@"tabbar_selected"]];
    [[UITabBarItem appearance] setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:
                                               [UIColor whiteColor], UITextAttributeTextColor,
                                               nil] forState:UIControlStateNormal];
    
    [[UITabBarItem appearance] setTitlePositionAdjustment:UIOffsetMake(0, -5)];
    
    
    [[UIBarButtonItem appearance] setTintColor:[UIColor colorWithRed:214/255.0 green:0/255.0 blue:11/255.0 alpha:1]];
 
    
    [[UINavigationBar appearance] setBackgroundImage:[UIImage imageNamed:@"nav_background"] forBarMetrics:UIBarMetricsDefault];
    
    [[UISlider appearance] setThumbImage:[UIImage imageNamed:@"control"] forState:UIControlStateNormal];
    
    UIImage *min = [[UIImage imageNamed:@"min"]
                                resizableImageWithCapInsets:UIEdgeInsetsMake(0, 2, 0, 2)];
    
    [[UISlider appearance] setMinimumTrackImage:min
                                       forState:UIControlStateNormal];
    
    [[UISlider appearance] setMinimumTrackImage:min
                                       forState:UIControlStateDisabled];
    
    UIImage *max = [[UIImage imageNamed:@"max"]
                    resizableImageWithCapInsets:UIEdgeInsetsMake(0, 2, 0, 2)];
    
    [[UISlider appearance] setMaximumTrackImage:max
                                       forState:UIControlStateNormal];
    
    [[UISlider appearance] setMaximumTrackImage:max
                                       forState:UIControlStateDisabled];
    
    
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
    UITabBarController *vc = [sb instantiateViewControllerWithIdentifier:@"TabBarViewController"];
    
    [self setupApplicationAudio:vc];
    
    
    [[UIApplication sharedApplication] beginReceivingRemoteControlEvents];
    [self.window becomeFirstResponder];
    
    return YES;
}
							
- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

-(void)logout
{
    [self removeAllData];
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
    UIViewController *vc = [sb instantiateViewControllerWithIdentifier:@"MainViewController"];
    self.window.rootViewController = vc;
}


#pragma mark - AlertView for error

-(void)alerViewWithText:(NSString*)error
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Oooops!" message:error delegate:self cancelButtonTitle:@"Ok" otherButtonTitles: nil];
    [alert show];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    [_api logout];
}

#pragma mark -
#pragma mark Audio Player

- (PlayerState)playOrPause {
    
    if (_player.rate == 0.0f) {
        NSLog(@"Play");
        [_player play];
        _playerState = Play;
    } else {
        NSLog(@"Pause");
        [_player pause];
        _playerState = Stop;
    }
    
    return _playerState;
}


-(void)playSong:(NSString*)songURL
{
    NSURL *url = [[NSURL alloc] initWithString:songURL];
	AVAsset *asset = [AVURLAsset URLAssetWithURL:url options:nil];
	NSArray *keys = [NSArray arrayWithObject:@"tracks"];
	[asset loadValuesAsynchronouslyForKeys:keys completionHandler:^(void) {
		NSError *error = nil;
        // get the status to see if the asset was loaded
		AVKeyValueStatus trackStatus = [asset statusOfValueForKey:@"tracks" error:&error];
		switch (trackStatus) {
			case AVKeyValueStatusLoaded:
				[self updateUserInterfaceForTracks];
                _playerItem = [AVPlayerItem playerItemWithAsset:asset];
		
                // Bug de iOS - https://devforums.apple.com/message/561368#561368
                [self performSelectorOnMainThread:@selector(replaceItem) withObject:nil waitUntilDone:TRUE];
                
                [_player play];
                _playerState = Play;
                
                if (self.nextSongIndex >= self.songsArray.count)
                    self.playerState = FinishPlayingList;
                
                _player.actionAtItemEnd = AVPlayerActionAtItemEndNone;
                                
                [[NSNotificationCenter defaultCenter] addObserver:self
                                                         selector:@selector(handleAVPlayerItemDidPlayToEndTimeNotification:)
                                                             name:AVPlayerItemDidPlayToEndTimeNotification
                                                           object:self.playerItem];
                self.isLoadingTrack = NO;
				break;
			case AVKeyValueStatusFailed:
				NSLog(@"Error: %@", [error localizedDescription]);
				break;
			case AVKeyValueStatusCancelled:
				NSLog(@"Canceled");
				break;
			default:
				break;
		}
	}];
}

-(void)updateUserInterfaceForTracks
{
    NSMutableDictionary *songInfo = [[NSMutableDictionary alloc] init];
    
    SDImageCache *cache = [SDImageCache sharedImageCache];
    
    UIImage *image = [cache imageFromKey:_currentTrack.imageBig];
    
    if (image != nil) {
        MPMediaItemArtwork *albumArt = [[MPMediaItemArtwork alloc] initWithImage:image];
        [songInfo setObject:albumArt forKey:MPMediaItemPropertyArtwork];
    }
    
    [songInfo setObject:self.currentTrack.titulo forKey:MPMediaItemPropertyTitle];
    [songInfo setObject:self.currentTrack.grupo forKey:MPMediaItemPropertyArtist];
    [songInfo setObject:self.currentTrack.album forKey:MPMediaItemPropertyAlbumTitle];

    [[MPNowPlayingInfoCenter defaultCenter] setNowPlayingInfo:songInfo];
}

//Bug de iOS - https://devforums.apple.com/message/561368#561368
-(void)replaceItem
{
    [_player replaceCurrentItemWithPlayerItem:self.playerItem];
}

-(void)playNextsong
{    
    if (self.nextSongIndex < self.songsArray.count) {
        self.isLoadingTrack = YES;
        [_player pause];
        Track *track = [_songsArray objectAtIndex:self.nextSongIndex];
        _currentTrack = track;
        NSString *url = [track url];
        self.nextSongIndex++;
        [self playSong:url];
    }
}

-(void)playPreviusSong
{
    self.nextSongIndex-=2;
        
    if (self.nextSongIndex < 0) {
        self.nextSongIndex = 0;
    }
        
    Track *track = [_songsArray objectAtIndex:self.nextSongIndex];
    _currentTrack = track;
    NSString *url = [track url];
    self.nextSongIndex++;
    [self playSong:url];
}

-(void)playSongAtIndex:(int)index
{
    self.nextSongIndex = index;
    [self playNextsong];
}

- (void)handleAVPlayerItemDidPlayToEndTimeNotification:(NSNotification *)notification {
    dispatch_async(dispatch_get_main_queue(), ^{
        _playerState = Stop;
        self.playNextSongTriggered = YES;
        self.triggredIndex = self.nextSongIndex;
        [self playNextsong];
	});
}

- (void)remoteControlReceivedWithEvent:(UIEvent *)receivedEvent {
    
    if (receivedEvent.type == UIEventTypeRemoteControl) {
        
        switch (receivedEvent.subtype) {
                
            case UIEventSubtypeRemoteControlTogglePlayPause:
                [self playOrPause];
                break;
                
            case UIEventSubtypeRemoteControlPreviousTrack:
                self.nextSongIndex-=2;
                
                if (self.nextSongIndex < 0) {
                    self.nextSongIndex = 0;
                }
                
                self.playNextSongTriggered = YES;
                self.triggredIndex = self.nextSongIndex;
                
                [self playPreviusSong];
                break;
                
            case UIEventSubtypeRemoteControlNextTrack:
                self.playNextSongTriggered = YES;
                self.triggredIndex = self.nextSongIndex;
                [self playNextsong];
                break;
                
            default:
                break;
        }
    }
}

#pragma mark -
#pragma mark Core Data

-(void)removeAllData
{
    NSPersistentStore *store = [[_persistentStoreCoordinator persistentStores] lastObject];
    NSError *error;
    NSURL *storeURL = store.URL;
    [_persistentStoreCoordinator removePersistentStore:store error:&error];
    [[NSFileManager defaultManager] removeItemAtPath:storeURL.path error:&error];
    
    
    NSLog(@"Data Reset");
    
    if (![_persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeURL options:nil error:&error]) {
        // do something with the error
    }
}

// Returns the managed object context for the application.
// If the context doesn't already exist, it is created and bound to the persistent store coordinator for the application.
- (NSManagedObjectContext *)managedObjectContext
{
    if (_managedObjectContext != nil) {
        return _managedObjectContext;
    }
    
    NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
    if (coordinator != nil) {
        _managedObjectContext = [[NSManagedObjectContext alloc] init];
        [_managedObjectContext setPersistentStoreCoordinator:coordinator];
    }
    return _managedObjectContext;
}

// Returns the managed object model for the application.
// If the model doesn't already exist, it is created from the application's model.
- (NSManagedObjectModel *)managedObjectModel
{
    if (_managedObjectModel != nil) {
        return _managedObjectModel;
    }
    NSURL *modelURL = [[NSBundle mainBundle] URLForResource:@"BandaBeat" withExtension:@"momd"];
    _managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:modelURL];
    return _managedObjectModel;
}

// Returns the persistent store coordinator for the application.
// If the coordinator doesn't already exist, it is created and the application's store added to it.
- (NSPersistentStoreCoordinator *)persistentStoreCoordinator
{
    if (_persistentStoreCoordinator != nil) {
        return _persistentStoreCoordinator;
    }
    
    NSURL *storeURL = [[self applicationDocumentsDirectory] URLByAppendingPathComponent:@"BandaBeat.sqlite"];
    
    NSDictionary *options = [NSDictionary dictionaryWithObjectsAndKeys:
                             [NSNumber numberWithBool:YES], NSMigratePersistentStoresAutomaticallyOption,
                             [NSNumber numberWithBool:YES], NSInferMappingModelAutomaticallyOption, nil];
    
    NSError *error = nil;
    _persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self managedObjectModel]];
    if (![_persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeURL options:options error:&error]) {
            NSLog(@"Unresolved error %@, %@", error, [error userInfo]);
            abort();
    }
    
    return _persistentStoreCoordinator;
}

// Returns the URL to the application's Documents directory.
- (NSURL *)applicationDocumentsDirectory
{
    return [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
}


#pragma mark -
#pragma mark Application Setup

- (void)setupApplicationAudio:(UITabBarController*)vc
{       
    // Se configura el AVPlayer
    _player = [[AVPlayer alloc] init];
    _playerState = NotInitialized;

    self.nextSongIndex = 0;
        
    // Se configura la sesión de audio
    [[AVAudioSession sharedInstance] setDelegate: self];
    [[AVAudioSession sharedInstance] setCategory: AVAudioSessionCategoryPlayback error: nil];
     
     UInt32 doSetProperty = 0;
    
     AudioSessionSetProperty (
                              kAudioSessionProperty_OverrideCategoryMixWithOthers,
                              sizeof (doSetProperty),
                              &doSetProperty
     );
    
    AudioSessionAddPropertyListener (
                                     kAudioSessionProperty_AudioRouteChange,
                                     audioRouteChangeListenerCallback,
                                     (__bridge void *)(self)
                                     );
    
    NSError *activationError = nil;
    [[AVAudioSession sharedInstance] setActive: YES error: &activationError];
}

-(void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self forKeyPath:AVPlayerItemDidPlayToEndTimeNotification];
}


#pragma mark -
#pragma mark Audio session callbacks


void audioRouteChangeListenerCallback (
                                       void                      *inUserData,
                                       AudioSessionPropertyID    inPropertyID,
                                       UInt32                    inPropertyValueSize,
                                       const void                *inPropertyValue
                                       ) {
    
    // ensure that this callback was invoked for a route change
    if (inPropertyID != kAudioSessionProperty_AudioRouteChange) return;
    
    AppDelegate *appDelegate = (__bridge AppDelegate*) inUserData;
    
    if(true){
        
        NSLog (@"Audio route change while application audio is stopped.");
        return;
        
    } else {
        
        CFDictionaryRef routeChangeDictionary = inPropertyValue;
        
        CFNumberRef routeChangeReasonRef =
        CFDictionaryGetValue (
                              routeChangeDictionary,
                              CFSTR (kAudioSession_AudioRouteChangeKey_Reason)
                              );
        
        SInt32 routeChangeReason;
        
        CFNumberGetValue (
                          routeChangeReasonRef,
                          kCFNumberSInt32Type,
                          &routeChangeReason
                          );
        if (routeChangeReason == kAudioSessionRouteChangeReason_OldDeviceUnavailable) {
            
            [appDelegate playOrPause];
            
            NSLog (@"Output device removed, so application audio was paused.");
            
        } else {
            NSLog (@"A route change occurred that does not require pausing of application audio.");
        }
    }
}

@end
