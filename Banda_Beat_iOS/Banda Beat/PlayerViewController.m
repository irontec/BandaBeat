//
//  PlayerViewController.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 24/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import "PlayerViewController.h"
#import "AppDelegate.h"
#import "UIImageView+WebCache.h"
#import "FXImageView.h"
#import "SDImageCache.h"

@interface PlayerViewController ()
@property(strong, nonatomic) id mTimeObserver;
@property(strong, nonatomic) SDImageCache *cache;
@property(nonatomic) BOOL buttonClicked;


-(void)syncScrubber;
-(void)loadTrackMetadata;

@end

@implementation PlayerViewController

float mRestoreAfterScrubbingRate;
BOOL isSended = NO;

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if ((self = [super initWithCoder:aDecoder]))
    {
        [self setUp];
    }
    return self;
}

- (void)setUp
{
    _cache = [SDImageCache sharedImageCache];
    _appDelegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
    _player = _appDelegate.player;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.trackedViewName = @"Player";
        
    [self.view setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"background"]]];

    
    PlayerState state = [_appDelegate playOrPause];
    if (state == Stop) {
        [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_play"] forState:UIControlStateNormal];
        [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_play_hi"] forState:UIControlStateSelected];
    } else {
        [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_pause"] forState:UIControlStateNormal];
        [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_pause_hi"] forState:UIControlStateSelected];
    }
    
    [_appDelegate.player addObserver:self forKeyPath:@"currentItem" options:NSKeyValueObservingOptionNew context:nil];    
    
    self.nextSongButton.enabled = NO;
    self.previusSongButton.enabled = NO;
    self.playButton.enabled = NO;
    self.slider.enabled = NO;
    
    _carousel.type = iCarouselTypeCoverFlow2;
    
}

-(void)viewWillAppear:(BOOL)animated
{
    if (_appDelegate.playerState == NotInitialized) {
        [_carousel reloadData];
    }
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    self.carousel = nil;
}


- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object
                        change:(NSDictionary *)change
                       context:(void *)context
{
    dispatch_async(dispatch_get_main_queue(), ^{
       
        [self loadTrackMetadata];
        
        if (_appDelegate.playNextSongTriggered == YES) {
            //Si estamos dentro es porque ha cambiado de canción automaticamente sin pulsar un botón
            _appDelegate.playNextSongTriggered = NO;
            //Ñapa para que no entre en bucle. Hemos entrado aqui sin pulsar botón pero indicamos que se ha pulsado un botón
            self.buttonClicked = YES;
            [_carousel scrollToItemAtIndex:_appDelegate.triggredIndex animated:YES];
        } else {
            [self updateView];
        }
        
        isSended = NO;
        
        if (!_mTimeObserver)
        {
            _mTimeObserver = [_player addPeriodicTimeObserverForInterval:CMTimeMakeWithSeconds(1, NSEC_PER_SEC) queue:nil usingBlock:
                              ^(CMTime time)
                              {
                                  [self syncScrubber];
                              }];
        }
    });
}

-(void)updateView
{
    self.playButton.enabled = YES;
    self.slider.enabled = YES;
    
    if (_appDelegate.nextSongIndex == _appDelegate.songsArray.count) {
        self.previusSongButton.enabled = YES;
        self.nextSongButton.enabled = NO;
    } else {
        self.previusSongButton.enabled = YES;
        self.nextSongButton.enabled = YES;
    }
    
    [_carousel setScrollEnabled:YES];
}

-(void)loadTrackMetadata
{
    self.songLabel.text = _appDelegate.currentTrack.titulo;
    
    NSString *album = _appDelegate.currentTrack.album;
    NSString *grupo = _appDelegate.currentTrack.grupo;
    NSMutableString *label = [[NSMutableString alloc] init];
    
    if(album == nil && grupo == nil) {
        [label appendString:@""];
    }
    
    if (grupo != nil) {
        [label appendString:grupo];
        
        if(album != nil) {
            [label appendString:@" - "];
            [label appendString:album];
        }
    } else {
        if(album != nil) {
            [label appendString:album];
        }
    }
    
    self.albumLabel.text = label;
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


-(void)removePlayerTimeObserver
{
    [_player removeTimeObserver:_mTimeObserver];
    _mTimeObserver = nil;
}

-(void)syncScrubber
{
    if (_appDelegate.playerState == Stop)
    {
        return;
    }
    
    CMTime endTime = CMTimeConvertScale (_player.currentItem.asset.duration, _player.currentTime.timescale, kCMTimeRoundingMethod_RoundHalfAwayFromZero);
    
    if (CMTimeCompare(endTime, kCMTimeZero) != 0 && !_slider.isTouchInside) {
        double normalizedTime = (double) _player.currentTime.value / (double) endTime.value;
        if (!isnan(normalizedTime)) {
            self.slider.value = normalizedTime;
        }
    }
    

    Float64 currentSeconds = CMTimeGetSeconds(_player.currentTime);
    
    int mins = currentSeconds/60.0;
    int secs = fmodf(currentSeconds, 60.0);
    
    NSString *minsString = mins < 10 ? [NSString stringWithFormat:@"0%d", mins] : [NSString stringWithFormat:@"%d", mins];
    NSString *secsString = secs < 10 ? [NSString stringWithFormat:@"0%d", secs] : [NSString stringWithFormat:@"%d", secs];
    
    _currentTimeLabel.text = [NSString stringWithFormat:@"%@:%@", minsString, secsString];
    
    Float64 endSeconds = CMTimeGetSeconds(endTime);
    
    mins = endSeconds/60.0;
    secs = fmodf(endSeconds, 60.0);
    
    minsString = mins < 10 ? [NSString stringWithFormat:@"0%d", mins] : [NSString stringWithFormat:@"%d", mins];
    secsString = secs < 10 ? [NSString stringWithFormat:@"0%d", secs] : [NSString stringWithFormat:@"%d", secs];
    
    _trackTimeLabel.text = [NSString stringWithFormat:@"%@:%@", minsString, secsString];
    
    if (!isSended && currentSeconds >= 60) {
       
        id<GAITracker> tracker = [[GAI sharedInstance] defaultTracker];
        
        [tracker trackEventWithCategory:@"Music"
                             withAction:@"Listen"
                              withLabel:_appDelegate.currentTrack.titulo
                              withValue:[NSNumber numberWithInt:1]];
        isSended = YES;
    }
}

- (void)dealloc
{
    //it's a good idea to set these to nil here to avoid
    //sending messages to a deallocated viewcontroller
    _carousel.delegate = nil;
    _carousel.dataSource = nil;
}

#pragma mark -
#pragma mark iCarousel methods

- (NSUInteger)numberOfItemsInCarousel:(iCarousel *)carousel
{
    //return the total number of items in the carousel
    return [_appDelegate.songsArray count];
}

- (UIView *)carousel:(iCarousel *)carousel viewForItemAtIndex:(NSUInteger)index reusingView:(UIView *)view
{
    //create new view if no view is available for recycling
    if (view == nil)
    {
        FXImageView *imageView = [[[FXImageView alloc] initWithFrame:CGRectMake(0, 0, 200.0f, 200.0f)] autorelease];
        imageView.contentMode = UIViewContentModeScaleAspectFit;
        imageView.asynchronous = YES;
        imageView.reflectionScale = 0.5f;
        imageView.reflectionAlpha = 0.25f;
        imageView.reflectionGap = 10.0f;
        imageView.shadowOffset = CGSizeMake(0.0f, 2.0f);
        imageView.shadowBlur = 5.0f;
        view = imageView;
    }
    
    Track *track = [_appDelegate.songsArray objectAtIndex:index];
    
    //show placeholder
    ((FXImageView *)view).processedImage = [UIImage imageNamed:@"placeholder_big"];
    
    NSString *imageURL = track.imageBig;
    
    UIImage *image = [_cache imageFromKey:imageURL];
    
    if (image == nil) {
        //set image with URL. FXImageView will then download and process the image
        [(FXImageView *)view setImageWithContentsOfURL:[NSURL URLWithString:imageURL]];
    } else {
        [(FXImageView *)view setImage:image];
    }

    
    return view;
}


- (void)carouselWillBeginDragging:(iCarousel *)carousel
{
    [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_pause"] forState:UIControlStateNormal];
    [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_pause_hi"] forState:UIControlStateSelected];
    
    self.previusSongButton.enabled = NO;
    self.nextSongButton.enabled = NO;
    self.playButton.enabled = NO;
}

- (void)carouselWillBeginScrollingAnimation:(iCarousel *)carousel;
{
    [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_pause"] forState:UIControlStateNormal];
    [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_pause_hi"] forState:UIControlStateSelected];
    
    self.previusSongButton.enabled = NO;
    self.nextSongButton.enabled = NO;
    self.playButton.enabled = NO;
}

- (void)carouselDidEndScrollingAnimation:(iCarousel *)carousel
{
    
    [_carousel setScrollEnabled:NO];
    
    if (self.buttonClicked == NO && _appDelegate.playNextSongTriggered == NO)
    {
        [_appDelegate playSongAtIndex:[carousel currentItemIndex]];
    }
    
    if (_appDelegate.playNextSongTriggered == YES) {
        NSInteger index = _appDelegate.triggredIndex;
        [_carousel scrollToItemAtIndex:index animated:YES];
        _appDelegate.playNextSongTriggered = NO;
        
    }
   
    self.buttonClicked = NO;
    
    //Comprobación para cuando se arranca la aplicación sin escuchar ninguna canción e bloquee la UI
    if ([_appDelegate.songsArray count] != 0)
         [self updateView];
    
   
}


#pragma mark - IBActions

-(void)playOrPause:(id)sender
{
    PlayerState state = [_appDelegate playOrPause];
    if (state == Stop) {
        [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_play"] forState:UIControlStateNormal];
        [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_play_hi"] forState:UIControlStateSelected];
    } else {
        [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_pause"] forState:UIControlStateNormal];
        [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_pause_hi"] forState:UIControlStateSelected];
    }
}

-(void)nextSong:(id)sender
{
    self.buttonClicked = TRUE;
        
    int index = self.carousel.currentItemIndex;
    
    [_carousel scrollToItemAtIndex:++index animated:YES];
    [_carousel setScrollEnabled:NO];
    
    [_appDelegate playNextsong];
}

-(void)previusSong:(id)sender
{
    self.buttonClicked = TRUE;
    
    [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_pause"] forState:UIControlStateNormal];
    [_playButton setBackgroundImage:[UIImage imageNamed:@"boton_pause_hi"] forState:UIControlStateSelected];
    
    int index = self.carousel.currentItemIndex;
    
    [_carousel scrollToItemAtIndex:--index animated:YES];
    [_carousel setScrollEnabled:NO];
    
    [_appDelegate playPreviusSong];
}

- (IBAction)beginScrubbing:(id)sender
{
    mRestoreAfterScrubbingRate = [_player rate];
    [_player setRate:0.f];
    
    _appDelegate.playerState = Play;
    
    /* Remove previous timer. */
    [self removePlayerTimeObserver];
}

- (IBAction)scrub:(id)sender
{
 
    CMTime playerDuration = _player.currentItem.asset.duration;
    
    if (CMTIME_IS_INVALID(playerDuration)) {
        return;
    }
    
    double duration = CMTimeGetSeconds(playerDuration);
    if (isfinite(duration))
    {
        float minValue = [_slider minimumValue];
        float maxValue = [_slider maximumValue];
        float value = [_slider value];
        
        double time = duration * (value - minValue) / (maxValue - minValue);
        
        [_player seekToTime:CMTimeMakeWithSeconds(time, NSEC_PER_SEC)];
    }
}

- (IBAction)endScrubbing:(id)sender
{
    if (!_mTimeObserver)
    {
        CMTime playerDuration = _player.currentItem.asset.duration;
        if (CMTIME_IS_INVALID(playerDuration))
        {
            return;
        }
        
        double duration = CMTimeGetSeconds(playerDuration);
        if (isfinite(duration))
        {
            CGFloat width = CGRectGetWidth([_slider bounds]);
            double tolerance = 0.5f * duration / width;
            
            _mTimeObserver = [_player addPeriodicTimeObserverForInterval:CMTimeMakeWithSeconds(tolerance, NSEC_PER_SEC) queue:nil usingBlock:
                              ^(CMTime time)
                              {
                                  [self syncScrubber];
                              }];
        }
    }
    
    if (mRestoreAfterScrubbingRate)
    {
        [_player setRate:mRestoreAfterScrubbingRate];
        mRestoreAfterScrubbingRate = 0.f;
    }
}

@end
