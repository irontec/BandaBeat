//
//  PlayerViewController.h
//  Banden Lehia
//
//  Created by Iker Mendilibar on 24/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import <QuartzCore/QuartzCore.h>
#import "GAITrackedViewController.h"
#import "AppDelegate.h"
#import "iCarousel.h"

@interface PlayerViewController : GAITrackedViewController <iCarouselDelegate>

@property(weak, nonatomic) AVPlayer *player;

@property(weak, nonatomic) IBOutlet UISlider *slider;
@property(weak, nonatomic) IBOutlet UILabel *trackTimeLabel;
@property(weak, nonatomic) IBOutlet UILabel *currentTimeLabel;
@property(weak, nonatomic) IBOutlet UILabel *albumLabel;
@property(weak, nonatomic) IBOutlet UILabel *songLabel;
@property(weak, nonatomic) IBOutlet UIButton *nextSongButton, *previusSongButton, *playButton;
@property(weak, nonatomic) IBOutlet iCarousel *carousel;

@property(strong, nonatomic) AppDelegate *appDelegate;

-(IBAction)endScrubbing:(id)sender;
-(IBAction)scrub:(id)sender;
-(IBAction)beginScrubbing:(id)sender;
-(IBAction)playOrPause:(id)sender;
-(IBAction)previusSong:(id)sender;
-(IBAction)nextSong:(id)sender;
@end
